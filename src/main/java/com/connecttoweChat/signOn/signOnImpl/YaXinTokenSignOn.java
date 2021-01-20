package com.connecttoweChat.signOn.signOnImpl;

import com.connecttoweChat.signOn.SignOnChain;
import com.connecttoweChat.signOn.SingleSignOn;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Service
public class YaXinTokenSignOn extends SingleSignOn {

    @Override
    public void sign(HttpServletRequest request, HttpServletResponse response, SignOnChain signOnChain) {
        log.info(producer());
        signOnChain.sign(request, response, false);
    }

    @Override
    public String producer() {
        return String.format("order:%d, YaXin sso!", order());
    }
}
