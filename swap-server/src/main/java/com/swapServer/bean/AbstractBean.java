package com.swapServer.bean;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import javax.persistence.MappedSuperclass;
import java.time.Instant;

@Data
@MappedSuperclass
public abstract class AbstractBean {
    @TableField(value = "create_date", fill = FieldFill.INSERT)
    private Instant createDate;
    @TableField(value = "create_user", fill = FieldFill.INSERT)
    private String createUser;
    @TableField(value = "update_user", fill = FieldFill.UPDATE)
    private String updateUser;
    @TableField(value = "update_date", fill = FieldFill.UPDATE)
    private Instant updateDate;

    public Instant getCreateDate(){return createDate;}
    public Instant getUpdateDate(){return updateDate;}
    public String getCreateUser(){return createUser;}
    public String getUpdateUser(){return updateUser;}
    public void setCreateDate(Instant createDate){this.createDate = createDate;}
    public void setUpdateDate(Instant updateDate){this.updateDate = updateDate;}
    public void setCreateUser(String createUser){this.createUser = createUser;}
    public void setUpdateUser(String updateUser){this.updateUser = updateUser;}
}
