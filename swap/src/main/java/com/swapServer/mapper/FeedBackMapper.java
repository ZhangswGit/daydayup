package com.swapServer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.swapServer.bean.FeedBack;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface FeedBackMapper extends BaseMapper<FeedBack> {
    List<FeedBack> findAllFeedBack();
    Optional<FeedBack> findFeedBackById(long id);
}
