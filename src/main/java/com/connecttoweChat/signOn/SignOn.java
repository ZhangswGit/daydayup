package com.connecttoweChat.signOn;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface SignOn {
    int order();

    void sign(HttpServletRequest request, HttpServletResponse response, SignOnChain signOnChain);
}
