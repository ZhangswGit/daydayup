package com.swapClient.window;

import com.swapClient.clent.NettyClientHandler;
import com.swapCommon.coding.Message;
import com.swapCommon.coding.MessageDecoder;
import com.swapCommon.coding.MessageEncoder;
import com.swapCommon.coding.MessageHead;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;


/**
 * @Data : 2021/02/22
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
@Slf4j
public class MainInterface extends JFrame {

    private JTextArea jTextArea;//

    private JTextField jTextField;//输入框

    private NorthJPanel northJPanel;

    private SouthJPanel southJPanel;

    public MainInterface(String goalName) {

        this.setTitle(goalName);
        this.setSize(600, 500);//窗口大小
        this.setLocation(200, 200);//窗口位置
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setResizable(false);

        northJPanel = new NorthJPanel();
        this.add(northJPanel, BorderLayout.NORTH);

        southJPanel = new SouthJPanel();
        this.add(southJPanel, BorderLayout.SOUTH);

        this.setVisible(true);
    }

    public void user1() {
        try {
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
                        }
                    });

            ChannelFuture channelFuture = bootstrap.connect("192.168.50.121", 8077).sync();
            log.info(" server start up on port : " + "nettyProperties.getHost()");

            channelFuture.channel().writeAndFlush(Message.builder().messageHead(MessageHead.AUTH).build());

            while (true) {
                System.out.println("输入信息：");
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                String input = br.readLine();
                channelFuture.channel().writeAndFlush(Message.builder()
                        .messageHead(MessageHead.MUTUAL)
                        .body(input).build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void user2() {

    }
}
