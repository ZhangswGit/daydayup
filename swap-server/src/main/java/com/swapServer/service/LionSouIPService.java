package com.swapServer.service;

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
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Slf4j
@Service
public class LionSouIPService {

    private DbSearcher dbSearcher = null;

    private DbConfig dbConfig = null;

    @PostConstruct
    public void init() {
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

    public LionSouIP getLionSouIP(String ip) {
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
        return LionSouIP.builder()
                .ip(ip)
                .country(split[0])
                .province(split[2])
                .city(split[3])
                .isp(split[4])
                .build();
    }

    @Data
    @Builder
    public static class LionSouIP {
        private String ip;

        private String country;

        private String province;

        private String city;

        private String isp;
    }
}
