package com.swapServer.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.swapServer.bean.SystemLog;
import com.swapServer.constants.OperationType;
import com.swapServer.model.response.UserModel;
import com.swapServer.service.SystemLogService;
import com.swapServer.transform.UserTransform;
import com.swapServer.utils.HeaderUtils;
import com.swapServer.bean.Organization;
import com.swapServer.bean.Role;
import com.swapServer.bean.User;
import com.swapServer.constants.ErrorAlertMessages;
import com.swapServer.error.BadRequestException;
import com.swapServer.mapper.OrganizationMapper;
import com.swapServer.mapper.RoleMapper;
import com.swapServer.mapper.UserMapper;
import com.swapServer.model.request.CreateUserRequest;
import com.swapServer.model.request.QueryUserRequest;
import com.swapServer.model.request.UpdateUserRequest;
import com.swapServer.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Data : 2020/12/25
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
@RestController
@RequestMapping("/admin/v1/user")
public class UserResourceController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private OrganizationMapper organizationMapper;

    @Autowired
    private SystemLogService systemLogService;

    @Autowired
    private UserTransform userTransform;

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<UserModel>> findAllUser(HttpServletRequest request, @Valid QueryUserRequest queryUserRequest, Pageable pageable) {
        IPage<User> userList = userService.findAllUser(queryUserRequest, pageable);
        HttpHeaders httpHeaders = HeaderUtils.generatePaginationHttpHeaders(userList);
        List<UserModel> userModels = userList.getRecords().stream().map(userTransform::toModel).collect(Collectors.toList());
        return new ResponseEntity(userModels, httpHeaders, HttpStatus.OK);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<UserModel> createUser(HttpServletRequest request, @Valid CreateUserRequest createUserRequest) {
        //判断数据是否合法
        Optional<User> userOpt = userMapper.findUserByPhoneOrEmail(createUserRequest.getEmail());
        if (userOpt.isPresent()) {
            throw new BadRequestException(ErrorAlertMessages.EmailExits);
        }
        userOpt = userMapper.findUserByPhoneOrEmail(createUserRequest.getPhone());
        if (userOpt.isPresent()) {
            throw new BadRequestException(ErrorAlertMessages.PhoneExits);
        }
        Optional<Role> roleOpt = roleMapper.findRoleById(createUserRequest.getRoleId());
        if (!roleOpt.isPresent()) {
            throw new BadRequestException(ErrorAlertMessages.RoleNotExits);
        }
        final Role roleOld = roleOpt.get();
        Optional<Organization> organizationOpt = organizationMapper.findOrganizationById(createUserRequest.getOrganizationId());
        if (!organizationOpt.isPresent()) {
            throw new BadRequestException(ErrorAlertMessages.OrganizationNotExits);
        }
        final Organization organizationOld = organizationOpt.get();
        User user = userService.createUser(createUserRequest);
        systemLogService.save(
                SystemLog.builder()
                        .type(OperationType.createUser)
                        .detail(
                                SystemLog.Item.builder()
                                        .itemType(SystemLog.ItemType.create)
                                        .itemDetails(
                                                SystemLog.ItemDetail.builder()
                                                        .value1("账号名称")
                                                        .value2(createUserRequest.getName())
                                                        .build(),
                                                SystemLog.ItemDetail.builder()
                                                        .value1("邮箱")
                                                        .value2(createUserRequest.getEmail())
                                                        .build(),
                                                SystemLog.ItemDetail.builder()
                                                        .value1("昵称")
                                                        .value2(createUserRequest.getNickName())
                                                        .build(),
                                                SystemLog.ItemDetail.builder()
                                                        .value1("电话")
                                                        .value2(createUserRequest.getPhone())
                                                        .build(),
                                                SystemLog.ItemDetail.builder()
                                                        .value1("角色")
                                                        .value2(roleOld.getRoleName())
                                                        .build(),
                                                SystemLog.ItemDetail.builder()
                                                        .value1("组织机构")
                                                        .value2(organizationOld.getName())
                                                        .build()
                                        ).build()).build());
        return ResponseEntity.ok(userTransform.toModel(user));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Long> deleteUser(HttpServletRequest request, @PathVariable("id") long id) {
        //判断数据是否合法
        Optional<User> userOpt = userMapper.findUserById(id);
        if (!userOpt.isPresent()) {
            throw new BadRequestException(ErrorAlertMessages.UserNotExits);
        }
        User userOld = userOpt.get();
        userService.deleteByLogic(id);
        systemLogService.save(SystemLog.builder()
                .type(OperationType.deleteUser)
                .detail(SystemLog.Item.builder()
                        .itemType(SystemLog.ItemType.delete)
                        .itemDetails(SystemLog.ItemDetail.builder()
                                .value1(userOld.getName())
                                .build())
                        .build()
                ).build());
        return ResponseEntity.ok(id);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<UserModel> updateUser(HttpServletRequest request, UpdateUserRequest updateUserRequest) {
        //判断数据是否合法
        Optional<User> userOpt = userMapper.findUserById(updateUserRequest.getId());
        if (!userOpt.isPresent()) {
            throw new BadRequestException(ErrorAlertMessages.UserNotExits);
        }
        User userOld = userOpt.get();
        userOpt = userMapper.findUserByPhoneOrEmailWithOutId(updateUserRequest.getEmail(), updateUserRequest.getId());
        if (userOpt.isPresent()) {
            throw new BadRequestException(ErrorAlertMessages.EmailExits);
        }
        userOpt = userMapper.findUserByPhoneOrEmailWithOutId(updateUserRequest.getPhone(), updateUserRequest.getId());
        if (userOpt.isPresent()) {
            throw new BadRequestException(ErrorAlertMessages.PhoneExits);
        }
        Optional<Role> roleOpt = roleMapper.findRoleById(updateUserRequest.getRoleId());
        if (!roleOpt.isPresent()) {
            throw new BadRequestException(ErrorAlertMessages.RoleNotExits);
        }
        Role roleOld = roleOpt.get();
        Optional<Organization> organizationOpt = organizationMapper.findOrganizationById(updateUserRequest.getOrganizationId());
        if (!organizationOpt.isPresent()) {
            throw new BadRequestException(ErrorAlertMessages.OrganizationNotExits);
        }
        Organization organizationOld = organizationOpt.get();
        User user = userService.updateUser(updateUserRequest);

        SystemLog systemLog = SystemLog.builder()
                .type(OperationType.updateUser)
                .detail(
                        SystemLog.Item.builder()
                                .itemType(SystemLog.ItemType.update)
                                .itemDetails(
                                        StringUtils.equals(updateUserRequest.getEmail(), user.getEmail()) ?
                                                SystemLog.ItemDetail.builder()
                                                        .value1(userOld.getEmail())
                                                        .value2(user.getEmail())
                                                        .build() : null,
                                        StringUtils.equals(updateUserRequest.getPhone(), user.getPhone()) ?
                                                SystemLog.ItemDetail.builder()
                                                        .value1(userOld.getPhone())
                                                        .value2(user.getPhone())
                                                        .build() : null,
                                        StringUtils.equals(updateUserRequest.getNickName(), user.getNickName()) ?
                                                SystemLog.ItemDetail.builder()
                                                        .value1(userOld.getNickName())
                                                        .value2(user.getNickName())
                                                        .build() : null,
                                        updateUserRequest.getRoleId() != user.getRoleId() ?
                                                SystemLog.ItemDetail.builder()
                                                        .value1(roleOld.getRoleName())
                                                        .value2(user.getRole().getRoleName())
                                                        .build() : null,
                                        updateUserRequest.getOrganizationId() != user.getOrganizationId() ?
                                                SystemLog.ItemDetail.builder()
                                                        .value1(organizationOld.getName())
                                                        .value2(user.getOrganization().getName())
                                                        .build() : null)
                                .build());
        systemLogService.save(systemLog);
        return ResponseEntity.ok(userTransform.toModel(user));
    }
}
