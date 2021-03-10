package com.swapServer.signOn.signOnImpl;

import com.swapServer.signOn.SignOnChain;
import com.swapServer.signOn.SingleSignOn;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Oauth2SignOn extends SingleSignOn {
    @Override
    public String producer() {
        return "oauth2.0";
    }

    @Override
    public void sign(HttpServletRequest request, HttpServletResponse response, SignOnChain signOnChain) {
        signOnChain.sign(request, response);
    }
}
