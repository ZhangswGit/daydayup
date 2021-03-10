package com.swapServer.service;

import com.swapServer.bean.SystemLog;
import com.swapServer.mapper.SystemLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SystemLogService extends MybatisPlusServiceEnhancer<SystemLogMapper, SystemLog>{

    @Autowired
    SystemLogMapper systemLogMapper;
}
