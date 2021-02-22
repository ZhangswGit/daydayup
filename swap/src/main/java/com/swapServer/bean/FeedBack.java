package com.swapServer.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.swapServer.constants.FeedbackType;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@TableName(value = "feed_back", autoResultMap = true)
@Builder
@Data
public class FeedBack extends AbstractBean{

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "title")
    private String title;

    @TableField(value = "description")
    private String description;

    @TableField(value = "feedback_type")
    private FeedbackType feedbackType;

    @TableField(value = "process_date")
    private Date processDate;

    @TableField(value = "screen_shots")
    private String screenShots;

    @TableField(value = "processor")
    private String processor;

    @TableField(value = "process_directions")
    private String processDirections;

}
