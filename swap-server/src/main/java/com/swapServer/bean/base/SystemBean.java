package com.swapServer.bean.base;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.swapServer.bean.base.AbstractBean;
import lombok.Data;

@Data
public class SystemBean extends AbstractBean {
    @TableField(value = "is_delete", condition = "%s=#{%s}")
    private long isDelete = 0l;

    @TableField(value = "is_system")
    @TableLogic(value = "0", delval = "1")
    private boolean isSystem;

}
