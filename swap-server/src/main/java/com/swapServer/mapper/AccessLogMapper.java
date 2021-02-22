package com.swapServer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.swapServer.bean.AccessLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccessLogMapper extends BaseMapper<AccessLog> {
}
