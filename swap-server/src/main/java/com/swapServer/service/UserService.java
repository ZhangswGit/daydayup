package com.swapServer.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.swapServer.bean.User;
import com.swapServer.config.security.MyUsernamePasswordAuthenticationToken;
import com.swapServer.constants.ErrorAlertMessages;
import com.swapServer.error.BadRequestException;
import com.swapServer.mapper.UserMapper;
import com.swapServer.model.request.CreateUserRequest;
import com.swapServer.model.request.QueryUserRequest;
import com.swapServer.model.request.UpdateUserRequest;
import com.swapServer.netty.Model.UserModel;
import com.swapServer.transform.UserTransform;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Data : 2020/12/25
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
@Slf4j
@Service
public class UserService extends MybatisPlusServiceEnhancer<UserMapper, User>{

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserTransform userTransform;

    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public User createUser(CreateUserRequest createUserRequest){
        User user = User.builder()
                .name(createUserRequest.getName())
                .nickName(createUserRequest.getNickName())
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
        if(StringUtils.isNotBlank(updateUserRequest.getEmail()) && !StringUtils.equals(updateUserRequest.getEmail(), oldUser.getEmail())){
            oldUser.setEmail(updateUserRequest.getEmail());
            update = true;
        }
        if(StringUtils.isNotBlank(updateUserRequest.getPhone()) && !StringUtils.equals(updateUserRequest.getPhone(), oldUser.getPhone())){
            oldUser.setPhone(updateUserRequest.getPhone());
            update = true;
        }
        if(StringUtils.isNotBlank(updateUserRequest.getNickName()) && !StringUtils.equals(updateUserRequest.getNickName(), oldUser.getNickName())){
            oldUser.setNickName(updateUserRequest.getNickName());
            update = true;
        }
        if(updateUserRequest.getOrganizationId() != 0 &&updateUserRequest.getOrganizationId() != oldUser.getOrganizationId()){
            oldUser.setOrganizationId(updateUserRequest.getOrganizationId());
            update = true;
        }
        if(updateUserRequest.getRoleId() != 0 && updateUserRequest.getRoleId() != oldUser.getRoleId()){
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

    //client端账号密码认证
    public UserModel auth(String userName, String passWord) {
        MyUsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new MyUsernamePasswordAuthenticationToken(userName, passWord);
        Authentication authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        UserModel userModel = Optional.ofNullable(authenticate).map(x -> {
            Optional<User> user = userMapper.findUserByNameOrPhoneOrEmail(userName);
            if (user.isPresent()) {
                log.debug("client userName:{},password:{} auth success", userName, passWord);
                return userTransform.toModel(user.get());
            }
            return null;
        }).orElse(null);

        if (userModel == null) {
            log.debug("client userName:{},password:{} auth fail", userName, passWord);
        }

        return userModel;
    }

    /**
     * client 获取当前系统所有用户
     * @return
     */
    public List<UserModel> findAllUser() {
        List<User> userList = userMapper.selectList(null);
        if (CollectionUtils.isEmpty(userList)) {
            return null;
        }
        List<UserModel> userModels = userList.stream().map(userTransform::toModel).collect(Collectors.toList());
        return userModels;
    }
}
