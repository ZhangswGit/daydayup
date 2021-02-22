package com.swapServer.bean;

import com.baomidou.mybatisplus.annotation.*;
import com.swapServer.constants.CatalogType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
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
