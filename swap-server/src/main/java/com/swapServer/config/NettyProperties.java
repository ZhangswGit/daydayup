package com.swapServer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @Data : 2020/12/25
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */

@Data
@Configuration("NettyProperties")
@PropertySource("classpath:config/server.properties")
@ConfigurationProperties(prefix = "netty", ignoreUnknownFields = false)
public class NettyProperties {

    private int port;
}
