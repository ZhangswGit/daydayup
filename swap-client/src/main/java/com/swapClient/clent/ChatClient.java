package com.swapClient.clent;

import com.swapClient.util.PropertiesUtils;
import com.swapCommon.Message;
import com.swapCommon.coding.MessageDecoder;
import com.swapCommon.coding.MessageEncoder;
import com.swapCommon.header.MessageHead;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @Data :  2021/2/26 14:16
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
@Slf4j
public class ChatClient {

    /**
     * 客户端用户Id
     */
    private long userId;

    private ChannelFuture channelFuture;

    private final String SERVER_IP = PropertiesUtils.getAsString("server.host");

    private final int SERVER_PORT = PropertiesUtils.getAsInteger("server.port");

    public ChatClient(long userId) {
        this.userId = userId;
    }

    public ChannelFuture getChannelFuture() {
        if (channelFuture != null) {
            return channelFuture;
        }
        start();
        log.info("client reconnection");
        return channelFuture;
    }

    public void start() {

        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        channel.pipeline().addLast("encoder", new MessageEncoder());
                        channel.pipeline().addLast("decoder", new MessageDecoder());
                        channel.pipeline().addLast(new NettyClientHandler());
                        channel.pipeline().addLast(new IdleStateHandler(5, 5, 0) {
                            @Override
                            protected void channelIdle(ChannelHandlerContext channelHandlerContext, IdleStateEvent evt) throws Exception {
                                log.info("heart to channel:{}", channelHandlerContext.channel().id());
                                channelHandlerContext.channel().writeAndFlush(Message.builder()
                                        .messageHead(MessageHead.HEART).build()).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                                super.channelIdle(channelHandlerContext, evt);
                            }
                        });
                    }
                });

        channelFuture = bootstrap.connect(SERVER_IP, SERVER_PORT).addListener((ChannelFutureListener) ch -> {
            if (ch.isSuccess()) {
                log.info("client request certification");
                ch.channel().writeAndFlush(Message.builder()
                        .messageHead(MessageHead.AUTH)
                        .localId(userId)
                        .build());
            }
        });
    }
}
