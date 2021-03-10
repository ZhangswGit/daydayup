package com.swapServer.signOn;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface SignOnChain {
    void sign(HttpServletRequest request, HttpServletResponse response);
}
