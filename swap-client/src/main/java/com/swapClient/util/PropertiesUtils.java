package com.swapClient.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @Data :  2021/3/2 13:43
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
@Slf4j
public class PropertiesUtils {

    private static Properties properties;

    static {
        run();
    }

    private synchronized static void run() {
        if (properties != null) {
            return;
        }
        properties = new Properties();

        InputStream in = PropertiesUtils.class.getClassLoader().getResourceAsStream("config.properties");
        try {
            properties.load(in);
        } catch (IOException e) {
            log.info("load config.properties failure");
        }
    }

    public static String getAsString(String key) {
        return properties.getProperty(key);
    }

    public static Integer getAsInteger(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }
}
