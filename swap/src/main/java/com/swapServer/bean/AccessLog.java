package com.swapServer.bean;

import com.baomidou.mybatisplus.annotation.*;
import com.swapServer.constants.LogType;
import lombok.Data;

import java.time.Instant;

@Data
@TableName("access_log")
public class AccessLog extends AbstractBean{
    @TableId(value = "id", type = IdType.AUTO)
    private long id;

    @TableField("ip")
    private String ip;

    @TableField("access_date")
    private Instant accessDate;

    @TableField("log_type")
    private LogType logType;

    @TableField("message")
    private String message;

    @TableField("details")
    private String details;

}
