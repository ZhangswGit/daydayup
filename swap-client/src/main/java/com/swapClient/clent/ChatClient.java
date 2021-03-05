package com.swapClient.clent;

import bean.Message;
import com.swapClient.util.PropertiesUtils;
import com.swapClient.window.LoginInterFace;
import com.swapClient.window.MainInterface;
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
     * 客户端窗口
     */
    private MainInterface mainInterface;

    /**
     * 登录窗口
     */
    private LoginInterFace loginInterFace;

    public ChatClient(MainInterface mainInterface, LoginInterFace loginInterFace) {
        this.mainInterface = mainInterface;
        this.loginInterFace = loginInterFace;
    }

    private final String SERVER_IP = PropertiesUtils.getAsString("server.host");

    private final int SERVER_PORT = PropertiesUtils.getAsInteger("server.port");


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
                        channel.pipeline().addLast(new NettyClientHandler(mainInterface, loginInterFace));
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

        bootstrap.connect(SERVER_IP, SERVER_PORT).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                mainInterface.setChannelFuture(future);
                loginInterFace.setChannelFuture(future);
            }
        });
    }
}
