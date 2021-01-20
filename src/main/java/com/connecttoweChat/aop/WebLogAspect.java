package com.connecttoweChat.aop;

import com.connecttoweChat.Utils.IPUtils;
import com.connecttoweChat.analysis.AnalysisEngine;
import com.connecttoweChat.analysis.access.AccessAction;
import com.sun.org.apache.regexp.internal.RE;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
@Slf4j
public class WebLogAspect {
    @Autowired
    private AnalysisEngine analysisEngine;

    @Pointcut("execution(* com.connecttoweChat.controller..*.*(..))")
    public void server(){}

    @Before("server()")
    public void validation(JoinPoint joinPoint){
        log.info("aop before do !");
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String clientAddress = IPUtils.getClientAddress(request);
        AccessAction accessAction = AccessAction.builder().ip(clientAddress).build();
        analysisEngine.execute(accessAction);
    }

}
