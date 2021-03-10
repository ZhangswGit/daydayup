package com.swapServer.bean;

import com.baomidou.mybatisplus.annotation.*;
import com.swapServer.bean.base.SystemBean;
import com.swapServer.constants.CatalogType;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Data
@Builder
@TableName("organization")
public class Organization extends SystemBean {

    @Tolerate
    public Organization(){}

    @TableId(value = "id", type = IdType.AUTO)
    private long id;

    @TableField("parent_id")
    private long parentId;

    @TableField("name")
    private String name;

    @Enumerated(EnumType.STRING)
    @TableField("type")
    private CatalogType type;

    @ManyToOne
    @TableField(exist = false)
    @JoinColumn(name = "parentId", nullable = false)
    private Organization parentOrganization;

}
