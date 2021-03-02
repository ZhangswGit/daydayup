package com.swapServer.netty.handler;

import com.swapCommon.Message;
import com.swapCommon.header.MessageHead;
import com.swapServer.constants.ErrorAlertMessages;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Data :  2021/2/26 13:45
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
@Slf4j
public class MessageHandler extends ChannelInboundHandlerAdapter {

    private static final Set<ChannelHandlerContext> clientCount = new HashSet<>();

    private static final ConcurrentHashMap<ChannelId, ChannelHandlerContext> channelHandlerContextMap = new ConcurrentHashMap<ChannelId, ChannelHandlerContext>();

    private static final ConcurrentHashMap<Long, ChannelId> userChannelIdMap = new ConcurrentHashMap<Long, ChannelId>();

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object obj) {
        if (!(obj instanceof Message)) {
            log.error("Message:{} format error", obj);
            offline(null, channelHandlerContext);
        }

        ChannelId channelId = channelHandlerContext.channel().id();
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
     * @param channelHandlerContext
     */
    @Override
    public void channelInactive(ChannelHandlerContext channelHandlerContext) {

        InetSocketAddress inetSocketAddress = (InetSocketAddress) channelHandlerContext.channel().remoteAddress();
        String clientIp = inetSocketAddress.getAddress().getHostAddress();
        int clientPort = inetSocketAddress.getPort();

        offline(null, channelHandlerContext);
        log.info("客户端[{}:{}]下线", clientIp, clientPort);
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
        log.info("{} --> 客户端[{}:{}]上线", Instant.now(), clientIp, clientPort);
    }

    void authenticateHandle(ChannelHandlerContext channelHandlerContext, Message message) {
        ChannelId channelIdOld = userChannelIdMap.get(message.getLocalId());
        if (channelIdOld != null) {
            //先下线已经登陆的用户
           offline(message, channelHandlerContextMap.get(channelIdOld));
        }
        //重新上线
        online(message, channelHandlerContext);
    }

    public void mutualHandle(ChannelHandlerContext channelHandlerContext, Message message) {
        if (!clientCount.contains(channelHandlerContext)) {
            log.error("local user:{} not certified", message.getLocalId());
            channelHandlerContext.writeAndFlush(Message.builder().messageHead(MessageHead.OFFLINE).body(ErrorAlertMessages.UserNotCertified).build());
            channelHandlerContext.close();
            return;
        }
        ChannelId channelId = userChannelIdMap.get(message.getGoalId());
        if (channelId == null) {
            log.error("goal user:{} offline", message.getGoalId());
            channelHandlerContext.writeAndFlush(Message.builder().messageHead(MessageHead.MUTUAL).body(ErrorAlertMessages.UserOffline).build());
            return;
        }
        ChannelHandlerContext goalChannelHandlerContext = channelHandlerContextMap.get(channelId);
        if (goalChannelHandlerContext == null) {
            log.error("goal user:{} offline", message.getGoalId());
            userChannelIdMap.remove(message.getGoalId());
            channelHandlerContext.writeAndFlush(Message.builder().messageHead(MessageHead.MUTUAL).body(ErrorAlertMessages.UserOffline).build());
            return;
        }
        log.info("localUser:{} --> goalUser:{}/ message:{}", message.getLocalId(), message.getGoalId(), message.getBody());
        //目标用户id 和 发送方用户id互换，用于客户端理解
        goalChannelHandlerContext.writeAndFlush(Message.builder()
                .messageHead(MessageHead.MUTUAL)
                .goalId(message.getLocalId())
                .localId(message.getGoalId())
                .body(message.getBody()).build());
    }

    /**
     * 上线
     * @param message
     * @param channelHandlerContext
     */
    void online (Message message, ChannelHandlerContext channelHandlerContext) {
        ChannelId channelId = channelHandlerContext.channel().id();
        channelHandlerContextMap.put(channelId, channelHandlerContext);
        userChannelIdMap.put(message.getLocalId(), channelId);
        clientCount.add(channelHandlerContext);
        channelHandlerContext.writeAndFlush(Message.builder()
                .messageHead(MessageHead.AUTH_SUCCESS)
                .body("asdasdasdas")
                .build());
        log.info("{} --> localUser:{} auth success; clientCount:{}", Instant.now(), message.getLocalId(), clientCount.size());
    }

    /**
     * 下线
     * @param message
     * @param channelHandlerContext
     */
    void offline (Message message, ChannelHandlerContext channelHandlerContext) {
        if (channelHandlerContext != null) {
            ChannelId channelId = channelHandlerContext.channel().id();
            channelHandlerContext.writeAndFlush(Message.builder().messageHead(MessageHead.OFFLINE).build());
            channelHandlerContext.close();
            channelHandlerContextMap.remove(channelId);
            clientCount.remove(channelHandlerContext);
        }
        if (message != null){
            userChannelIdMap.remove(message.getLocalId());
        }
        log.info("{} --> offline; clientCount:{}", Instant.now(), clientCount.size());
    }

}
