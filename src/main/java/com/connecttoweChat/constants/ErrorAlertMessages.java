package com.connecttoweChat.constants;

public enum ErrorAlertMessages {
    ServerError("抱歉，服务发生异常！"),
    ParamError("参数不合法！"),
    RoleNotExits("角色不存在!"),
    UserNotExits("账号不存在!"),
    OrganizationNotExits("组织不存在！"),
    EmailExits("邮箱已占用"),
    PhoneExits("手机号已占用"),
    UserOrPassWordError("账号或密码错误！请重试");

    private String alias;
    ErrorAlertMessages(String alias){
        this.alias = alias;
    }
    public String getAlias(){
        return this.alias;
    }
}
