package com.swapServer.service;

import com.swapServer.bean.Resource;
import com.swapServer.mapper.ResourceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *@Data : 2021/01/25
 *@Author : zhangsw
 *@Descripe : TODO
 *@Version : 0.1
 */

@Service
public class ResourceService extends MybatisPlusServiceEnhancer<ResourceMapper, Resource> {

    @Autowired
    private ResourceMapper resourceMapper;

}
