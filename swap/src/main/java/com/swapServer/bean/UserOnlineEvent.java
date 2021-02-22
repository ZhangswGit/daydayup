package com.swapServer.bean;

import org.springframework.context.ApplicationEvent;

/**
 *@Data : 2020/12/25
 *@Author : zhangsw
 *@Descripe : TODO
 *@Version : 0.1
 */
public class UserOnlineEvent extends ApplicationEvent {

    private String userName;
    private String ip;

    public UserOnlineEvent(Object source) {
        super(source);
    }

    public UserOnlineEvent(Object source, String ip, String userName){
        super(source);
        this.ip = ip;
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
