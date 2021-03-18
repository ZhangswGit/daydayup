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
@PropertySource(value = NettyProperties.NETTY_PROPERTIES_PATH, ignoreResourceNotFound = true)
@ConfigurationProperties(prefix = "netty", ignoreUnknownFields = false)
public class NettyProperties {

    public static final String NETTY_PROPERTIES_PATH = "file:/home/swap/config/server.properties";

    private int port = 8077;

    private String filePath;
}
