package com.swapServer.netty;

import com.swapCommon.Message;
import com.swapCommon.coding.MessageDecoder;
import com.swapCommon.coding.MessageEncoder;
import com.swapCommon.header.MessageHead;
import com.swapServer.config.NettyProperties;
import com.swapServer.netty.handler.MessageHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.InetSocketAddress;

/**
 * @Data :  2021/2/26 10:56
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
@Slf4j
@Component
public class NettyServer {

    @Resource(name = "NettyProperties")
    private NettyProperties nettyProperties;

    public void start() throws Exception{
        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();
        bootstrap.group(boss,work)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer(){

                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        channel.pipeline().addLast(new MessageEncoder());
                        channel.pipeline().addLast(new MessageDecoder());
                        channel.pipeline().addLast(new MessageHandler());
                    }
                });

        bootstrap.bind(new InetSocketAddress(nettyProperties.getPort())).sync();
        System.out.println(" server start up on port : " + nettyProperties.getPort());
    }

    public static void main(String[] args) {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            EventLoopGroup boss = new NioEventLoopGroup();
            EventLoopGroup work = new NioEventLoopGroup();
            bootstrap.group(boss,work)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.AUTO_READ, true)
                    .childHandler(new ChannelInitializer(){

                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            channel.pipeline().addLast(new MessageEncoder());
                            channel.pipeline().addLast(new MessageDecoder());
                            channel.pipeline().addLast(new MessageHandler());
                            channel.pipeline().addLast(new IdleStateHandler(30, 30, 0) {
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

            ChannelFuture future = bootstrap.bind("192.168.50.121", 8077).sync();
            System.out.println(" server start up on port : " + 8077);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
