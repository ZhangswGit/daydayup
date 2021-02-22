package com.swapServer.bean;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Builder;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
@Builder
@TableName("resource")
public class Resource extends SystemBean{

    @TableId(value = "id", type = IdType.AUTO)
    private long id;

    private String resourceName;

    private String resourcePath;

    @Enumerated(EnumType.STRING)
//    private String type;

    private long parentId;

    @TableField(exist = false)
    private Resource parentResource;

}
