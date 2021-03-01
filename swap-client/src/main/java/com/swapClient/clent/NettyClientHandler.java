package com.swapClient.clent;

/**
 * @Data :  2021/2/26 15:36
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */


import com.swapCommon.coding.Message;
import com.swapCommon.coding.MessageHead;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) {

        ChannelId channelId = channelHandlerContext.channel().id();
        log.info("{} --  ClientHandler :{} Active", Instant.now(), channelId);
    }

    /**
     * @param channelHandlerContext
     * @author xiongchuan on 2019/4/28 16:10
     * @DESCRIPTION: 有服务端端终止连接服务器会触发此函数
     * @return: void
     */
    @Override
    public void channelInactive(ChannelHandlerContext channelHandlerContext) {

        ChannelId channelId = channelHandlerContext.channel().id();
        channelHandlerContext.close();
        log.info("{} -- ClientHandler :{} closed", Instant.now(), channelId);
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) {
        if (!(msg instanceof Message)) {
           log.error("Message:{} format error", msg);
            channelHandlerContext.close();
        }
        Message message = (Message) msg;
        switch (message.getMessageHead()) {
            case MessageHead.AUTH_SUCCESS :
                log.info("服务端认证成功");
                break;
            case MessageHead.MUTUAL :
                log.info(message.getBody().toString());
                log.info("{} --> {} 发送消息 {}", Instant.now(), message.getGoalId(), message.getBody());
                break;
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) {

        log.info("服务端:[{}]发生异常[]", channelHandlerContext.channel().id(), cause.getMessage());
        channelHandlerContext.close();
    }
}


