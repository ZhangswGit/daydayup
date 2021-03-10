package com.swapServer.bean;

import com.baomidou.mybatisplus.annotation.*;
import com.swapServer.bean.base.SystemBean;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Data
@Builder
@TableName("user")
public class User extends SystemBean {

    @Tolerate
    public User(){}

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

    @ManyToOne
    @TableField(exist = false)
    @JoinColumn(name = "organizationId", nullable = false)
    private Organization organization;

    @ManyToOne
    @TableField(exist = false)
    @JoinColumn(name = "roleId", nullable = false)
    private Role role;

}
