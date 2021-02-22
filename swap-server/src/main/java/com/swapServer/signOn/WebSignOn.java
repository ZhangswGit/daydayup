package com.swapServer.signOn;

import com.swapServer.config.jwtToken.TokenProvide;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class WebSignOn implements SignOn{
    public final String WEB_COOKIE_NAME = TokenProvide.WEB_COOKIE_NAME;

    @Override
    public int order(){
        return 0;
    }

}
