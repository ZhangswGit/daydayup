package com.swapServer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @Data : 2020/12/25
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */

@Data
@Configuration("AliyunProperties")
@PropertySource(value = AliyunProperties.ALIYUN_PROPERTIES_PATH, ignoreResourceNotFound = true)
@ConfigurationProperties(prefix = "aliyun", ignoreUnknownFields = false)
public class AliyunProperties {

    public static final String ALIYUN_PROPERTIES_PATH = "file:/home/swap/config/aliyun.properties";

    private String regionId;
    private String accessKeyId;
    private String accessKeySecret;
    private String domainName;
    private String type;
    private String line;
    private String value;
}
