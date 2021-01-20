package com.connecttoweChat.signOn.signOnImpl;

import com.connecttoweChat.signOn.SignOnChain;
import com.connecttoweChat.signOn.WebSignOn;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Slf4j
@Service
public class PhoneSignOn extends WebSignOn {

    @Override
    public void sign(HttpServletRequest request, HttpServletResponse response, SignOnChain signOnChain) {
        log.info(describe());
        signOnChain.sign(request, response, false);
    }

    public String describe(){
        return String.format("order:%d, phone sign!", order());
    }

    @Override
    public int order(){
        return 3;
    }

}
