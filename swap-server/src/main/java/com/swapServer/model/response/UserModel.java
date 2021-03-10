package com.swapServer.model.response;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Builder;
import lombok.Data;

/**
 * @Data :  2021/3/10 15:41
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
@Data
@Builder
public class UserModel {

    private long id;

    private String name;

    private String nickName;

    private String email;

    private String phone;

    private long roleId;

    private long organizationId;
}
