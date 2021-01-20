package com.connecttoweChat.signOn.signOnImpl;

import com.connecttoweChat.config.jwtToken.TokenProvide;
import com.connecttoweChat.config.security.MyUsernamePasswordAuthenticationToken;
import com.connecttoweChat.mapper.UserMapper;
import com.connecttoweChat.signOn.SignOnChain;
import com.connecttoweChat.signOn.WebSignOn;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Service
public class UserPassWordAndCodeSignOn extends WebSignOn {

    @Autowired
    TokenProvide tokenProvide;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public void sign(HttpServletRequest request, HttpServletResponse response, SignOnChain signOnChain) {
        log.info(String.format("order:%d, user and password sign!", order()));
        String userName = request.getParameter("name");
        String passWord = request.getParameter("passWord");
        try {
            MyUsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new MyUsernamePasswordAuthenticationToken(userName, passWord);
            Authentication authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authenticate);
            String token = tokenProvide.createToken(authenticate);
            Cookie tokenCookie = new Cookie(WEB_COOKIE_NAME, token);
            tokenCookie.setPath("/");
            tokenCookie.setHttpOnly(true);
            response.addCookie(tokenCookie);
            signOnChain.sign(request, response, true);
            return;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("name:{}/password:{} login fail ! {}", userName, passWord, e.getMessage());
        }
        signOnChain.sign(request, response, false);
    }
}
