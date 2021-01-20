package com.connecttoweChat.config;

import com.connecttoweChat.bean.UserOnlineEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
/**
 *@Data : 2020/12/25
 *@Author : zhangsw
 *@Descripe : TODO
 *@Version : 0.1
 */
@Configuration
@Slf4j
public class ServerApplicationEventConfiguration {

    @EventListener
    public void createLog(UserOnlineEvent userOnlineEvent){
        log.info("%s-%s 上线！",userOnlineEvent.getUserName(), userOnlineEvent.getIp());
    }

}
