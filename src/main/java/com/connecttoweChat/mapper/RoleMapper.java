package com.connecttoweChat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.connecttoweChat.bean.Role;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {
    List<Role> findAllRole();
    Optional<Role> findRoleById(long id);
}
