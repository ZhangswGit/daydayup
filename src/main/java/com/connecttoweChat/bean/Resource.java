package com.connecttoweChat.bean;

import com.baomidou.mybatisplus.annotation.*;
import com.connecttoweChat.constants.CatalogType;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
@TableName("resource")
public class Resource extends SystemBean{

    @TableId(value = "id", type = IdType.AUTO)
    private long id;

    @TableField("resource_name")
    private String resourceName;

    @TableField("resource_path")
    private String resourcePath;

    @TableField("type")
    private CatalogType type;

    @TableField("parent_id")
    private long parentId;

    @TableField(exist = false)
    private Resource parentResource;

}
