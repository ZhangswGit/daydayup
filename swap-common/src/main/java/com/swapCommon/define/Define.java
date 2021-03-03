package com.swapCommon.define;

public enum Define {
    normal(1000, "正常"),
    goalUserOffline(1001, "目标用户未上线！");

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
