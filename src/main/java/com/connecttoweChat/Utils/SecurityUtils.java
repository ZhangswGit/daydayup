package com.connecttoweChat.Utils;

import com.connecttoweChat.config.jwtToken.TokenProvide;
import io.jsonwebtoken.Jwts;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public class SecurityUtils {

    @Autowired
    TokenProvide tokenProvide;

    public static final String WEB_COOKIE_NAME = "web_cookie";

    public static String currentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (String) Optional.ofNullable(authentication).map(authentication1 -> {
            Object principal = authentication1.getPrincipal();
            if (principal instanceof String) {
                return (String) principal;
            } else if (principal instanceof UserDetails){
                return ((UserDetails) principal).getUsername();
            } else {
                return null;
            }
        }).orElse("");
    }

    public static void loginOut(HttpServletRequest request, HttpServletResponse response){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(ObjectUtils.isNotEmpty(authentication)){
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        Cookie[] cookies = request.getCookies();
        if(ObjectUtils.isNotEmpty(cookies)){
            for (Cookie cookie: cookies){
                cookie.setMaxAge(0);
            }
        }
    }

}
