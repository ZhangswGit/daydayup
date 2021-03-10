package com.swapClient.clent;

import bean.Message;
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

    //服务器地址
    private String serverHost = "127.0.0.1";

    //服务端口
    private int serverPort = 8077;

    /**
     * 客户端窗口
     */
    private MainInterface mainInterface;

    /**
     * 登录窗口
     */
    private LoginInterFace loginInterFace;

    public ChatClient(MainInterface mainInterface, LoginInterFace loginInterFace, String serverHost, int serverPort) {
        this.mainInterface = mainInterface;
        this.loginInterFace = loginInterFace;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public ChatClient(MainInterface mainInterface, LoginInterFace loginInterFace) {
        this.mainInterface = mainInterface;
        this.loginInterFace = loginInterFace;
    }

    //重启次数
    private static int restartTimes = 0;

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

        bootstrap.connect(serverHost, serverPort).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                mainInterface.setChannelFuture(future);
                loginInterFace.setChannelFuture(future);
                restartTimes = 0;
            } else if(restartTimes > 2) {
                log.error("client start fail ,please check the configuration!");
                mainInterface.closed();
                loginInterFace.closed();
            } else {
                //重启
                log.debug("reconnect : [{}:{}]", serverHost, serverPort);
                Thread.sleep(restartTimes * 10000l);
                restartTimes ++;
                start();
            }
        });
    }
}
