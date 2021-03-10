package com.swapServer.signOn;

import com.swapServer.config.jwtToken.TokenProvide;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;

@Slf4j
public abstract class WebSignOn implements SignOn{

    public final String WEB_COOKIE_NAME = TokenProvide.WEB_COOKIE_NAME;

    @Autowired
    protected TokenProvide tokenProvide;

    @Autowired
    protected AuthenticationManager authenticationManager;

    @Override
    public int order(){
        return 0;
    }

}
