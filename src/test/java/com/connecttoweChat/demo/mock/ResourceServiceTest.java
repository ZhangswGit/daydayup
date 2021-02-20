package com.connecttoweChat.demo.mock;

import com.connecttoweChat.bean.Resource;
import com.connecttoweChat.mapper.ResourceMapper;
import com.connecttoweChat.service.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Data : 2020/12/25
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
@Slf4j
public class ResourceServiceTest extends AbstractBeanTest {

    @Autowired
    private ResourceMapper resourceMapper;

    @Test
    public void findAllResource(){
        List<Resource> allResource = resourceMapper.findAllResourceByRoleId(1l);
        log.info(allResource + "");
    }
}
