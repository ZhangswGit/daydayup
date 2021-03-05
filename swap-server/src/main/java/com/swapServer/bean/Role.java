package com.swapServer.bean;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.List;

@Data
@Builder
@TableName("role")
public class Role extends SystemBean {

    @Tolerate
    public Role(){}

    @TableId(value = "id", type = IdType.AUTO)
    private long id;

    @TableField(value = "role_name")
    private String roleName;

    @TableField(value = "parent_id")
    private long parentId;

    @TableField(exist = false)
    private List<Resource> resources;

    @ManyToOne
    @TableField(exist = false)
    @JoinColumn(name = "parentId", nullable = false)
    private Role parentRole;
}
