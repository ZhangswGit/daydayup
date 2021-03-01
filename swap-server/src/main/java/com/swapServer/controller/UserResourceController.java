package com.swapServer.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
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

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<User>> findAllUser(HttpServletRequest request, @Valid QueryUserRequest queryUserRequest, Pageable pageable){
        IPage<User> userList = userService.findAllUser(queryUserRequest, pageable);
        HttpHeaders httpHeaders = HeaderUtils.generatePaginationHttpHeaders(userList);
        List<User> userModels = userList.getRecords();
        return new ResponseEntity(userModels, httpHeaders, HttpStatus.OK);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Long> createUser(HttpServletRequest request, @Valid CreateUserRequest createUserRequest){
        //判断数据是否合法
        Optional<User> userOpt = userMapper.findUserByPhoneOrEmail(createUserRequest.getEmail());
        if (userOpt.isPresent()){
            throw new BadRequestException(ErrorAlertMessages.EmailExits);
        }
        userOpt = userMapper.findUserByPhoneOrEmail(createUserRequest.getPhone());
        if (userOpt.isPresent()){
            throw new BadRequestException(ErrorAlertMessages.PhoneExits);
        }
        Optional<Role> roleOpt = roleMapper.findRoleById(createUserRequest.getRoleId());
        if (!roleOpt.isPresent()){
            throw new BadRequestException(ErrorAlertMessages.RoleNotExits);
        }
        Optional<Organization> organizationOpt = organizationMapper.findOrganizationById(createUserRequest.getOrganizationId());
        if (!organizationOpt.isPresent()){
            throw new BadRequestException(ErrorAlertMessages.OrganizationNotExits);
        }
        User user = userService.createUser(createUserRequest);
        return ResponseEntity.ok(user.getId());
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Long> deleteUser(HttpServletRequest request, @PathVariable("id") long id){
        //判断数据是否合法
        Optional<User> userOpt = userMapper.findUserById(id);
        if (!userOpt.isPresent()){
            throw new BadRequestException(ErrorAlertMessages.UserNotExits);
        }
        userService.deleteByLogic(id);
        return ResponseEntity.ok(id);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Long> updateUser(HttpServletRequest request, UpdateUserRequest updateUserRequest){
        //判断数据是否合法
        Optional<User> userOpt = userMapper.findUserById(updateUserRequest.getId());
        if (!userOpt.isPresent()){
            throw new BadRequestException(ErrorAlertMessages.UserNotExits);
        }
        userOpt = userMapper.findUserByPhoneOrEmailWithOutId(updateUserRequest.getEmail(), updateUserRequest.getId());
        if (userOpt.isPresent()){
            throw new BadRequestException(ErrorAlertMessages.EmailExits);
        }
        userOpt = userMapper.findUserByPhoneOrEmailWithOutId(updateUserRequest.getPhone(), updateUserRequest.getId());
        if (userOpt.isPresent()){
            throw new BadRequestException(ErrorAlertMessages.PhoneExits);
        }
        Optional<Role> roleOpt = roleMapper.findRoleById(updateUserRequest.getRoleId());
        if (!roleOpt.isPresent()){
            throw new BadRequestException(ErrorAlertMessages.RoleNotExits);
        }
        Optional<Organization> organizationOpt = organizationMapper.findOrganizationById(updateUserRequest.getOrganizationId());
        if (!organizationOpt.isPresent()){
            throw new BadRequestException(ErrorAlertMessages.OrganizationNotExits);
        }
        User user = userService.updateUser(updateUserRequest);
        return ResponseEntity.ok(user.getId());
    }
}
