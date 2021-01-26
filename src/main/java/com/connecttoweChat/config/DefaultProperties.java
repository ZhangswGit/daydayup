package com.connecttoweChat.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Data : 2020/12/25
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */

@Data
@Component
@ConfigurationProperties(prefix = "aliyun", ignoreUnknownFields = false)
public class DefaultProperties {
    private String regionId;
    private String accessKeyId;
    private String accessKeySecret;
    private String domainName;
    private String type;
    private String line;
    private String serverIp;
}
