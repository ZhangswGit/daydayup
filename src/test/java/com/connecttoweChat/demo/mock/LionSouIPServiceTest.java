package com.connecttoweChat.demo.mock;

import com.connecttoweChat.demo.mock.AbstractBeanTest;
import com.connecttoweChat.service.LionSouIPService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Data : 2020/12/25
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
@Slf4j
public class LionSouIPServiceTest extends AbstractBeanTest {
    
    @Autowired
    LionSouIPService lionSouIPService;
    
    @Test
    public void getIp(){
        String ip = "103.85.173.226";
        String county = lionSouIPService.getCountry(ip);
        log.info(county);
    }
}
