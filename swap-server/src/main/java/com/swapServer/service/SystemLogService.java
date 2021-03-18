package com.swapServer.service;

import com.swapServer.bean.SystemLog;
import com.swapServer.mapper.SystemLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SystemLogService extends MybatisPlusServiceEnhancer<SystemLogMapper, SystemLog>{

    @Autowired
    SystemLogMapper systemLogMapper;

    /**
     * 日志入库
     * 没有详情信息 默认未作任何操作，不予入库
     * @param systemLog 日志实体
     * @return
     */
    @Override
    public boolean save(SystemLog systemLog) {
        if (StringUtils.isBlank(systemLog.getDetail())) {
            return false;
        }
        return super.save(systemLog);
    }
}
