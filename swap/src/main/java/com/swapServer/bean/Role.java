package com.swapServer.bean;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@TableName("role")
public class Role extends SystemBean {

    @TableId(value = "id", type = IdType.AUTO)
    private long id;

    @TableField("role_name")
    private String roleName;

    @TableField("parent_id")
    private long parentId;

    @TableField(exist = false)
    private List<Resource> resources;

    @TableField(exist = false)
    private Role parentRole;
}
