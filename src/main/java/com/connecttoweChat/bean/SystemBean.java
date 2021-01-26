package com.connecttoweChat.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

@Data
public class SystemBean extends AbstractBean{
    @TableField(value = "is_delete")
    private long isDelete;

    @TableField(value = "is_system")
    @TableLogic(value = "0", delval = "1")
    private boolean isSystem;

}
