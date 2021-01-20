package com.connecttoweChat.model.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * @Data : 2020/12/25
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
@Data
public class UpdateUserRequest {

    @NotNull
    private long id;

    @NotNull
    private String nickName;

    @NotNull
    private String phone;

    @NotNull
    @javax.validation.constraints.Email
    private String Email;

    @NotNull
    private long roleId;

    @NotNull
    private long organizationId;
}
