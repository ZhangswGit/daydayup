package com.swapServer.netty.handler;

import com.swapCommon.coding.Message;
import com.swapCommon.coding.MessageHead;
import com.swapServer.constants.ErrorAlertMessages;
import com.swapServer.error.BadRequestException;
import com.swapServer.error.UserOfflineException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Data :  2021/2/26 13:45
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
@Slf4j
public class MessageHandler extends ChannelInboundHandlerAdapter {

    private static final ConcurrentHashMap<ChannelId, ChannelHandlerContext> channelHandlerContextMap = new ConcurrentHashMap<ChannelId, ChannelHandlerContext>();

    private static final ConcurrentHashMap<Long, ChannelId> userChannelIdMap = new ConcurrentHashMap<Long, ChannelId>();

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object obj) {
        ChannelId channelId = channelHandlerContext.channel().id();
        if (!(obj instanceof Message)) {
            log.error("Message:{} format error", obj);
            channelHandlerContext.close();
            channelHandlerContextMap.remove(channelId);
        }
        log.info("channelId: {}, data: {}", channelId, obj);
        Message message = (Message) obj;

        switch (message.getMessageHead()) {
            case MessageHead.AUTH:
                authenticateHandle(channelHandlerContext, message);
                break;
            case MessageHead.MUTUAL:
                mutualHandle(channelHandlerContext, message);
                break;
        }
    }

    /**
     * 客户端断开连接触发
     *
     * @param ctx
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {

        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = inetSocketAddress.getAddress().getHostAddress();
        int clientPort = inetSocketAddress.getPort();

        ChannelId channelId = ctx.channel().id();
        channelHandlerContextMap.remove(channelId);
        log.info("{} --> 客户端[{}:{}]下线 --> ChannelId:[{}]", Instant.now(), clientIp, clientPort, channelId);
    }

    /**
     * @param channelHandlerContext
     * @author xiongchuan on 2019/4/28 16:10
     * @DESCRIPTION: 有客户端连接服务器会触发此函数
     * @return: void
     */
    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) {

        InetSocketAddress inetSocketAddress = (InetSocketAddress) channelHandlerContext.channel().remoteAddress();
        String clientIp = inetSocketAddress.getAddress().getHostAddress();
        int clientPort = inetSocketAddress.getPort();
        channelHandlerContext.channel().read();//

        //获取连接通道唯一标识
        ChannelId channelId = channelHandlerContext.channel().id();
        channelHandlerContextMap.put(channelId, channelHandlerContext);
        log.info("{} --> 客户端[{}:{}]上线 --> ChannelId:[{}]", Instant.now(), clientIp, clientPort, channelId);
    }

    void authenticateHandle(ChannelHandlerContext ch, Message message) {
        ChannelId channelId = userChannelIdMap.get(message.getLocalId());
        if (channelId != null) {
            ChannelHandlerContext channelHandlerContext = channelHandlerContextMap.get(channelId);
            if (channelHandlerContext != null) {
                channelHandlerContext.writeAndFlush(Message.builder().messageHead(MessageHead.OFFLINE).build());
                channelHandlerContext.close();
                channelHandlerContextMap.remove(channelId);
            }
        }

        ChannelId id = ch.channel().id();
        channelHandlerContextMap.put(id, ch);
        userChannelIdMap.put(message.getLocalId(), id);
        ch.writeAndFlush(Message.builder().messageHead(MessageHead.AUTH_SUCCESS).build());
        log.error("localUser:{} auth success", message.getLocalId());
    }

    public void mutualHandle(ChannelHandlerContext ch, Message message) {
        ChannelId channelId = userChannelIdMap.get(message.getGoalId());
        if (channelId == null) {
            log.error("goal user:{} offline", message.getGoalId());
            ch.writeAndFlush(Message.builder().messageHead(MessageHead.MUTUAL).body(ErrorAlertMessages.UserOffline).build());
        }
        ChannelHandlerContext channelHandlerContext = channelHandlerContextMap.get(channelId);
        if (channelHandlerContext == null) {
            log.error("goal user:{} offline", message.getGoalId());
            userChannelIdMap.remove(message.getGoalId());
            ch.writeAndFlush(Message.builder().messageHead(MessageHead.MUTUAL).body(ErrorAlertMessages.UserOffline).build());
        }
        log.error("localUser:{} --> goalUser:{}/ message:{}", message.getLocalId(), message.getGoalId(), message.getBody());
        //目标用户id 和 发送方用户id互换，用于客户端理解
        channelHandlerContext.writeAndFlush(Message.builder()
                .goalId(message.getLocalId())
                .localId(message.getGoalId())
                .body(message.getBody()).build());
    }

}
