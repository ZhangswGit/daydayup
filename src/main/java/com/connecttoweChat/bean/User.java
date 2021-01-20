package com.connecttoweChat.bean;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import javax.validation.constraints.Size;
import java.time.Instant;

@Data
@TableName("user")
public class User extends SystemBean{

    @TableId(value = "id", type = IdType.AUTO)
    private long id;

    @TableField("name")
    private String name;

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
