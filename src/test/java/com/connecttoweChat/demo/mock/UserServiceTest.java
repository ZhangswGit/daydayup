package com.connecttoweChat.demo.mock;

import com.connecttoweChat.bean.User;
import com.connecttoweChat.mapper.UserMapper;
import com.connecttoweChat.model.request.CreateUserRequest;
import com.connecttoweChat.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.Optional;

/**
 * @Data : 2020/12/25
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
@Slf4j
public class UserServiceTest extends AbstractBeanTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Test
    public void createUser() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setName("zsw");
        createUserRequest.setEmail("13367@qq.com");
        createUserRequest.setPassword("123456");
        createUserRequest.setOrganizationId(1);
        createUserRequest.setPhone("17600945506");
        createUserRequest.setRoleId(1);

        User user = userService.createUser(createUserRequest);
        log.info("insert user:{}", user);
    }

    @Test
    public void findUser() {
        Optional<User> userOpt = userMapper.findUserById(4);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.getCreateDate();
        }
    }
}

