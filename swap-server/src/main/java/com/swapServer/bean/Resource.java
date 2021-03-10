package com.swapServer.bean;

import com.baomidou.mybatisplus.annotation.*;
import com.swapServer.bean.base.SystemBean;
import com.swapServer.constants.CatalogType;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import javax.persistence.*;

@Data
@Builder
@TableName("resource")
public class Resource extends SystemBean {

    @Tolerate
    public Resource(){}

    @TableId(value = "id", type = IdType.AUTO)
    private long id;

    @TableField(value = "resource_name")
    private String resourceName;

    @TableField(value = "resource_path")
    private String resourcePath;

    @Enumerated(EnumType.STRING)
    @TableField(value = "type")
    private CatalogType type;

    @TableField(value = "parent_id")
    private long parentId;

    @ManyToOne
    @TableField(exist = false)
    @JoinColumn(name = "parentId", nullable = false)
    private Resource parentResource;

}
