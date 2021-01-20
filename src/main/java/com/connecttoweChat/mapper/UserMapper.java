package com.connecttoweChat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.connecttoweChat.bean.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    List<User> findAllUser();
    Optional<User> findUserById(long id);
    Optional<User> findUserByNameOrPhoneOrEmail(String unique);
}
