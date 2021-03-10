package com.swapServer.signOn.signOnImpl;

import com.swapServer.signOn.SignOnChain;
import com.swapServer.signOn.WebSignOn;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Service
public class PhoneSignOn extends WebSignOn {

    @Override
    public void sign(HttpServletRequest request, HttpServletResponse response, SignOnChain signOnChain) {
        log.info(describe());
        signOnChain.sign(request, response);
    }

    public String describe(){
        return String.format("order:%d, phone sign!", order());
    }

    @Override
    public int order(){
        return 3;
    }

}
