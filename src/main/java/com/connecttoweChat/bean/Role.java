package com.connecttoweChat.bean;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import org.apache.ibatis.type.Alias;
import org.springframework.core.annotation.AliasFor;

import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.time.Instant;
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
