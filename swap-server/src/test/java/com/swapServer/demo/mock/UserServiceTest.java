package com.swapServer.demo.mock;

import com.swapServer.bean.SystemLog;
import com.swapServer.bean.User;
import com.swapServer.constants.OperationType;
import com.swapServer.mapper.UserMapper;
import com.swapServer.model.request.CreateUserRequest;
import com.swapServer.model.request.UpdateUserRequest;
import com.swapServer.service.SystemLogService;
import com.swapServer.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
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

    @Autowired
    private SystemLogService systemLogService;

    @Test
    public void createUser() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setName("ryh1");
        createUserRequest.setNickName("任玉辉");
        createUserRequest.setEmail("133681@qq.com");
        createUserRequest.setPassword("123456");
        createUserRequest.setOrganizationId(1);
        createUserRequest.setPhone("17600945507");
        createUserRequest.setRoleId(1);

        User user = userService.createUser(createUserRequest);

        systemLogService.save(SystemLog.builder()
                .detail(SystemLog.Item.builder()
                        .itemType(SystemLog.ItemType.create)
                        .itemDetails(SystemLog.ItemDetail.builder().value1("名字").value2("ryh").build(),
                                SystemLog.ItemDetail.builder().value1("昵称").value2("任玉辉").build())
                        .build())
                .type(OperationType.createUser)
                .build());

        log.info("insert user:{}", user);
    }

    @Test
    public void findUser() {
        Optional<User> userOpt = userMapper.findUserById(3);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            log.info("user :{}", user);
        }
    }

    @Test
    public void updateUser() {
        Optional<User> userOpt = userMapper.findUserById(8l);
        if (userOpt.isPresent()) {
            User userOld = userOpt.get();

            UpdateUserRequest updateUserRequest = new UpdateUserRequest();
            updateUserRequest.setId(8l);
            updateUserRequest.setPhone("17600944407");
            updateUserRequest.setEmail("zsw122@163.com");
            User user = userService.updateUser(updateUserRequest);

            systemLogService.save(SystemLog.builder()
                    .type(OperationType.updateUser)
                    .detail(SystemLog.Item.builder()
                            .itemType(SystemLog.ItemType.update)
                            .itemDetails(
                                    SystemLog.ItemDetail.builder()
                                            .value1(userOld.getPhone())
                                            .value2(user.getPhone())
                                            .build(),
                                    SystemLog.ItemDetail.builder()
                                            .value1(userOld.getEmail())
                                            .value2(user.getEmail())
                                            .build())
                            .build())
                    .build());

            log.info("update user:{}", user);
        }
    }
}

