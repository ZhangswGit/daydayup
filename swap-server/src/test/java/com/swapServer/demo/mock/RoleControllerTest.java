package com.swapServer.demo.mock;

import com.swapServer.bean.Resource;
import com.swapServer.bean.Role;
import com.swapServer.mapper.RoleMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

/**
 *@Data : 2020/12/25
 *@Author : zhangsw
 *@Descripe : role 测试
 *@Version : 0.1
 */
@Slf4j
public class RoleControllerTest extends AbstractBeanTest {

    @Autowired
    private RoleMapper roleMapper;

    @Test
    void createRole(){
        Role role = Role.builder()
                .parentRole(Role.builder().id(1).build())
                .roleName("测试角色")
                .resources(Arrays.asList(Resource.builder().id(1).build(), Resource.builder().id(2).build()))
                .build();
        roleMapper.insert(role);
    }

}
