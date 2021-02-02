package com.connecttoweChat.config;

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
@Configuration
@PropertySource("classpath:config/aliyun.properties")
@ConfigurationProperties(prefix = "aliyun", ignoreUnknownFields = false)
public class DefaultProperties {

    private String regionId;
    private String accessKeyId;
    private String accessKeySecret;
    private String domainName;
    private String type;
    private String line;
    private String value;
}
