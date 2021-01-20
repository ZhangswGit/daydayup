package com.connecttoweChat.bean;

import com.baomidou.mybatisplus.annotation.*;
import com.connecttoweChat.constants.CatalogType;
import lombok.Data;

import java.time.Instant;

@Data
@TableName("organization")
public class Organization extends SystemBean {

    @TableId(value = "id", type = IdType.AUTO)
    private long id;

    @TableField("parent_id")
    private long parentId;

    @TableField("name")
    private String name;

    @TableField("type")
    private CatalogType type;

    @TableField(exist = false)
    private Organization parentOrganization;

}
