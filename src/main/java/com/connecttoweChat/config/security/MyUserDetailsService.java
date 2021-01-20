package com.connecttoweChat.config.security;

import com.connecttoweChat.bean.User;
import com.connecttoweChat.constants.AuthConstant;
import com.connecttoweChat.constants.ErrorAlertMessages;
import com.connecttoweChat.error.BadRequestException;
import com.connecttoweChat.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        Optional<User> userOpt = userMapper.findUserByNameOrPhoneOrEmail(name);
        if(!userOpt.isPresent()){
            throw new BadRequestException(ErrorAlertMessages.UserOrPassWordError);
        }
        User user = userOpt.get();
        List<GrantedAuthority> grantedAuthorities = Arrays.asList(new GrantedAuthority[]{new SimpleGrantedAuthority(AuthConstant.ADMIN)});
        return new org.springframework.security.core.userdetails.User(user.getName(), user.getPassWord(), grantedAuthorities);
    }
}
