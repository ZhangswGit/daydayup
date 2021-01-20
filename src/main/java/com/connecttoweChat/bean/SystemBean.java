package com.connecttoweChat.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

@Data
public class SystemBean extends AbstractBean{
    @TableField(value = "is_delete")
    @TableLogic(value = "0", delval = "1")
    private boolean isDelete;

    @TableField(value = "is_system")
    private boolean isSystem;

}
