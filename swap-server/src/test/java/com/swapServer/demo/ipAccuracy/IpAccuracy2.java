package com.swapServer.demo.ipAccuracy;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * @Data :  2021/3/19 17:23
 * @Author : zhangsw
 * @Descripe : IP准确性测试
 *  从多个网站确认同一 ip
 *  与项目使用ip库做比对
 * @Version : 0.1
 */
@Slf4j
public class IpAccuracy2 {

    static String ips[] = new String[]{
            "1.85.9.20","117.179.249.217","45.251.23.122","58.208.154.170","114.242.26.177","171.8.189.98","103.85.173.226"
    };

    public static void main(String[] args) {

        Arrays.asList(ips).forEach(ip -> {
            IpAccuracyChain ipAccuracyChain = new IpAccuracyChain();
            ipAccuracyChain.accuracy(ip);
            log.info("current ip {} isAccurate:{}", ip, ipAccuracyChain.isAccurate());
        });
    }
}
