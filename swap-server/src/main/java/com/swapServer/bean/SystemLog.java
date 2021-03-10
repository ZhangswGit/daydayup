package com.swapServer.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.swapServer.constants.CatalogType;
import com.swapServer.constants.OperationType;
import lombok.experimental.Tolerate;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * @Data :  2021/3/9 17:20
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
//@TableName("systemLog")
public class SystemLog {

    @Tolerate
    public SystemLog(){}

    @TableId(value = "id", type = IdType.AUTO)
    private long id;

    @Enumerated(EnumType.STRING)
    @TableField(value = "type")
    private OperationType type;

}
