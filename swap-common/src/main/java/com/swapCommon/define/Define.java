package com.swapCommon.define;

public enum Define {
    normal(1000, "正常"),
    goalUserOffline(1001, "目标用户未上线！"),
    noUserSelected(1002, "未选择用户！"),
    userOrPassWordError(1003,"账号或密码错误！"),
    userForceOffline(1003,"用户被强制下线！");

    private int status;
    private String detail;

    Define(int status, String detail){
        this.status = status;
        this.detail = detail;
    }

    public String getDetail() {
        return detail;
    }
}
