package com.connecttoweChat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.connecttoweChat.bean.User;
import com.connecttoweChat.model.request.QueryUserRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    IPage<User> findAllUser(@Param("query") QueryUserRequest queryUserRequest, IPage<User> page);
    Optional<User> findUserById(long id);
    Optional<User> findUserByNameOrPhoneOrEmail(String unique);
    Optional<User> findUserByPhoneOrEmail(String unique);
    Optional<User> findUserByPhoneOrEmailWithOutId(String unique, long id);
    void deleteByLogic(long id);
}
