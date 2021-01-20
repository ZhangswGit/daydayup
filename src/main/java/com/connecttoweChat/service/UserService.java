package com.connecttoweChat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.connecttoweChat.bean.User;
import com.connecttoweChat.constants.ErrorAlertMessages;
import com.connecttoweChat.error.BadRequestException;
import com.connecttoweChat.mapper.UserMapper;
import com.connecttoweChat.model.request.CreateUserRequest;
import com.connecttoweChat.model.request.QueryUserRequest;
import com.connecttoweChat.model.request.UpdateUserRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

/**
 * @Data : 2020/12/25
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
public class UserService extends MybatisPlusServiceEnhancer<UserMapper, User>{
    @Autowired
    UserMapper userMapper;

    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public User createUser(CreateUserRequest createUserRequest){
        User user = User.builder()
                .name(createUserRequest.getName())
                .passWord(bCryptPasswordEncoder.encode(createUserRequest.getPassword()))
                .phone(createUserRequest.getPhone())
                .email(createUserRequest.getEmail())
                .organizationId(createUserRequest.getOrganizationId())
                .roleId(createUserRequest.getRoleId())
                .build();
        userMapper.insert(user);
        return user;
    }

    public User updateUser(UpdateUserRequest updateUserRequest){
        Optional<User> userOpt = userMapper.findUserById(updateUserRequest.getId());
        if (!userOpt.isPresent()){
            throw new BadRequestException(ErrorAlertMessages.UserNotExits);
        }
        User oldUser = userOpt.get();
        boolean update = false;
        if(!StringUtils.equals(updateUserRequest.getEmail(), oldUser.getEmail())){
            oldUser.setEmail(updateUserRequest.getEmail());
            update = true;
        }
        if(!StringUtils.equals(updateUserRequest.getPhone(), oldUser.getPhone())){
            oldUser.setPhone(updateUserRequest.getPhone());
            update = true;
        }
        if(!StringUtils.equals(updateUserRequest.getNickName(), oldUser.getNickName())){
            oldUser.setNickName(updateUserRequest.getNickName());
            update = true;
        }
        if(updateUserRequest.getOrganizationId() != oldUser.getOrganizationId()){
            oldUser.setOrganizationId(updateUserRequest.getOrganizationId());
            update = true;
        }
        if(updateUserRequest.getRoleId() != oldUser.getRoleId()){
            oldUser.setRoleId(updateUserRequest.getRoleId());
            update = true;
        }
        if (update){
            userMapper.updateById(oldUser);
        }
        return oldUser;
    }

    public long deleteByLogic(long id){
        userMapper.deleteByLogic(id);
        return id;
    }

    public IPage<User> findAllUser(QueryUserRequest queryUserRequest, Pageable pageable){
        IPage<User> userIPage = convert(pageable);
        return userMapper.findAllUser(queryUserRequest, userIPage);
    }
}
