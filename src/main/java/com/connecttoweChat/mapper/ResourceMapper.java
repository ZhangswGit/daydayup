package com.connecttoweChat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.connecttoweChat.bean.Resource;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ResourceMapper extends BaseMapper<Resource> {
    List<Resource> findAllResource();
}
