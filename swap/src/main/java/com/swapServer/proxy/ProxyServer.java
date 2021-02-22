package com.swapServer.proxy;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import org.springframework.context.annotation.Configuration;

/**
 * @Data : 2020/12/25
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
@Configuration
public class ProxyServer {

    private ServerBootstrap bootstrap = new ServerBootstrap();

    private NioEventLoopGroup serverBossGroup = new NioEventLoopGroup();



}
