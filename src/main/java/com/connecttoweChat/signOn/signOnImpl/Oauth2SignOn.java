package com.connecttoweChat.signOn.signOnImpl;

import com.connecttoweChat.signOn.SignOnChain;
import com.connecttoweChat.signOn.SingleSignOn;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Oauth2SignOn extends SingleSignOn {
    @Override
    public String producer() {
        return "oauth2.0";
    }

    @Override
    public void sign(HttpServletRequest request, HttpServletResponse response, SignOnChain signOnChain) {

    }
}
