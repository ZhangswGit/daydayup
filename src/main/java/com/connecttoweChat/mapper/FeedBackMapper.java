package com.connecttoweChat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.connecttoweChat.bean.FeedBack;
import com.connecttoweChat.bean.User;
import liquibase.pro.packaged.F;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface FeedBackMapper extends BaseMapper<FeedBack> {
    List<FeedBack> findAllFeedBack();
    Optional<FeedBack> findFeedBackById(long id);
}
