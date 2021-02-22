package com.swapServer.bean;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@TableName("user")
public class User extends SystemBean{

    @TableId(value = "id", type = IdType.AUTO)
    private long id;

    @TableField("name")
    private String name;

    @TableField("nick_name")
    private String nickName;

    @TableField("pass_word")
    private String passWord;

    @TableField("email")
    private String email;

    @TableField("phone")
    private String phone;

    @TableField("role_id")
    private long roleId;

    @TableField("organization_id")
    private long organizationId;

    @TableField(exist = false)
    private Organization organization;

    @TableField(exist = false)
    private Role role;

}
