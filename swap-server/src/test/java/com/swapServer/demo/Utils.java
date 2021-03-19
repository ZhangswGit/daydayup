package com.swapServer.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.swapServer.demo.ipAccuracy.Site;
import com.swapServer.service.LionSouIPService;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.lionsoul.ip2region.DataBlock;
import org.lionsoul.ip2region.DbConfig;
import org.lionsoul.ip2region.DbMakerConfigException;
import org.lionsoul.ip2region.DbSearcher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;

/**
 * @Data :  2021/3/19 18:24
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
@Slf4j
public class Utils {

    private static DbSearcher dbSearcher = null;

    private static DbConfig dbConfig = null;

    public static ObjectMapper objectMapper = new ObjectMapper();

    public static JsonObject jsonObject = new JsonObject();

    static {
        try {
            Resource resource = new ClassPathResource("config/resource/ip2region.db");
            dbConfig = new DbConfig();
            dbSearcher = new DbSearcher(dbConfig, resource.getFile().getPath());
            log.debug("Loaded COUNTRY GeoIP db");
        } catch (IOException e) {
            log.error("Failed to load COUNTRY GeoIP db: {}", e.getMessage());
        } catch (DbMakerConfigException e) {

        }
    }

    public static Site getLionSouIP(String ip) {
        DataBlock dataBlock = null;
        try {
            dataBlock = dbSearcher.binarySearch(ip);
        } catch (IOException e) {
            log.warn("Invalid ip address for country '{}' - {}", ip, e.getMessage());
            return null;
        }
        if (dataBlock == null) {
            return null;
        }
        //pattern:国家|区域|省份|城市|ISP_
        String region = dataBlock.getRegion();

        if (StringUtils.isBlank(region)) {
            return null;
        }

        String[] split = StringUtils.split(region, "|");
        return Site.builder()
                .country(split[0])
                .province(split[2])
                .city(split[3])
                .build();
    }
}
