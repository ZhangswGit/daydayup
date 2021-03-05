package com.swapServer.demo.mock;

import com.swapServer.bean.Organization;
import com.swapServer.mapper.OrganizationMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Data :  2021/3/5 9:21
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
@Slf4j
public class OrganizationServiceTest extends AbstractBeanTest {

    @Autowired
    private OrganizationMapper organizationMapper;

    @Test
    public void findAllOrganization() {
        List<Organization> allOrganization = organizationMapper.findAllOrganization();
        log.info("allOrganization:{}", allOrganization);
    }
}
