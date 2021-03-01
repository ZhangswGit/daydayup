package com.swapClient.clent;

import com.swapCommon.coding.MessageDecoder;
import com.swapCommon.coding.MessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @Data :  2021/2/26 14:16
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
public class ChatClient {

    public static ChannelFuture channelFuture;

    public static void start() throws Exception {
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup work = new NioEventLoopGroup();
        bootstrap.group(work)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer(){
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        channel.pipeline().addLast("encoder", new MessageEncoder());
                        channel.pipeline().addLast("decoder", new MessageDecoder());
                        channel.pipeline().addLast(new NettyClientHandler());
                    }
                });

        channelFuture = bootstrap.connect("192.168.50.121", 8077).sync();
        System.out.println(" server start up on port : " + "nettyProperties.getHost()");
    }
}
