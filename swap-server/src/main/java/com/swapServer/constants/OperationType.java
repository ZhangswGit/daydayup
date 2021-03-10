package com.swapServer.constants;

public enum OperationType {
    createUser("创建账户"),
    updateUser("修改账户"),
    createRole("创建角色"),
    deleteUser("删除账户");

    private String alias;

    OperationType(String alias) {
        this.alias = alias;
    }

    public String getAlias(){return this.alias;}
}
