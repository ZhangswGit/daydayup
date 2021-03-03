package com.swapServer.service;

import bean.SwapUser;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.swapServer.bean.User;
import com.swapServer.constants.ErrorAlertMessages;
import com.swapServer.error.BadRequestException;
import com.swapServer.mapper.UserMapper;
import com.swapServer.model.request.CreateUserRequest;
import com.swapServer.model.request.QueryUserRequest;
import com.swapServer.model.request.UpdateUserRequest;
import com.swapServer.netty.Model.UserModel;
import com.swapServer.transform.UserTransform;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Data : 2020/12/25
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
@Service
public class UserService extends MybatisPlusServiceEnhancer<UserMapper, User>{

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserTransform userTransform;

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
        this.save(user);
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
            updateById(oldUser);
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

    public UserModel auth(String userName, String passWord) {
//        Optional<User> user = userMapper.findUserByNameOrPhoneOrEmail(userName);
//        if (user.isPresent()) {
//            return userTransform.toModel(user.get());
//        }
//        return null;
        Map<String, UserModel> users = new HashMap<String, UserModel>();
        users.put("张三", UserModel.builder().userName("张三").userId(12138l).build());
        users.put("李四", UserModel.builder().userName("李四").userId(333888l).build());
        users.put("王五", UserModel.builder().userName("王五").userId(222444).build());
        return users.get(userName);
    }

    public List<UserModel> findAllUser() {
        List<UserModel> users = new ArrayList<>();
        users.add(UserModel.builder().userName("张三").userId(12138l).build());
        users.add(UserModel.builder().userName("李四").userId(333888l).build());
        users.add(UserModel.builder().userName("王五").userId(222444).build());
        return users;
    }
}
