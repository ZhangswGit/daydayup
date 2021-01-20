package com.connecttoweChat.constants;

public enum ErrorAlertMessages {
    ServerError("抱歉，服务发生异常！"),
    ParamError("参数不合法！"),
    UserOrPassWordError("账号或密码错误！请重试");

    private String alias;
    ErrorAlertMessages(String alias){
        this.alias = alias;
    }
    public String getAlias(){
        return this.alias;
    }
}
