package com.swapServer.signOn;

import com.swapServer.utils.IPUtils;
import com.swapServer.utils.SecurityUtils;
import com.swapServer.bean.system.UserOnlineEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SignOnChainImpl implements SignOnChain {

    @Autowired
    private ApplicationContext applicationContext;

    private List<SignOn> signOnList;

    private int index = 0;

    @Override
    public void sign(HttpServletRequest request, HttpServletResponse response) {
        String currentUser = SecurityUtils.currentUser();
        if (StringUtils.isNotBlank(currentUser)) {
            String clientAddress = IPUtils.getClientAddress(request);
            UserOnlineEvent userOnlineEvent = new UserOnlineEvent(SecurityContextHolder.getContext().getAuthentication(), currentUser, clientAddress);
            applicationContext.publishEvent(userOnlineEvent);
            log.info("{} is login", signOnList.get(index - 1).getClass());
            index = 0;
            return;
        } else if (index == signOnList.size()) {
            index = 0;
        } else {
            index++;
            signOnList.get(index - 1).sign(request, response, this);
        }
    }

    @PostConstruct
    void run() {
        Map<String, SignOn> beansOfType = applicationContext.getBeansOfType(SignOn.class);
        this.signOnList = beansOfType.values().stream().sorted(Comparator.comparing(signOn -> signOn.order())).collect(Collectors.toList());
    }

}
