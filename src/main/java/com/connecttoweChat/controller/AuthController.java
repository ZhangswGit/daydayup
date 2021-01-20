package com.connecttoweChat.controller;

import com.connecttoweChat.Utils.SecurityUtils;
import com.connecttoweChat.bean.Role;
import com.connecttoweChat.bean.User;
import com.connecttoweChat.constants.AuthConstant;
import com.connecttoweChat.mapper.ResourceMapper;
import com.connecttoweChat.mapper.RoleMapper;
import com.connecttoweChat.mapper.UserMapper;
import com.connecttoweChat.signOn.SignOnChain;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/admin/v1")
public class AuthController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    SignOnChain signOnChainImpl;

    @GetMapping("/auth")
    public String login(HttpServletRequest request, HttpServletResponse response) {
        signOnChainImpl.sign(request, response);
        String username = SecurityUtils.currentUser();
        if (StringUtils.isEmpty(username)) {
            return "";
        }
        return "true";
    }

    @GetMapping("/loginOut")
    public String loginOut(HttpServletRequest request, HttpServletResponse response) {
        SecurityUtils.loginOut(request, response);
        return "true";
    }

    @GetMapping("/code")
    @Secured({AuthConstant.ADMIN})
    public String sendCode(HttpServletRequest request, HttpServletResponse response) {
        String username = SecurityUtils.currentUser();
        resourceMapper.deleteById(4);
        List<User> allUser = userMapper.findAllUser();
        List<Role> allRole = roleMapper.findAllRole();
        Role role = roleMapper.findRoleById(1l);
        return username;
    }
}
