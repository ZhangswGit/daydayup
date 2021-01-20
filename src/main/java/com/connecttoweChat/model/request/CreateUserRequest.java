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
public class CreateUserRequest {

    @NotNull
    private String name;

    @NotNull
    private String password;

    @NotNull
    private String phone;

    @NotNull
    @Email
    private String Email;

    @NotNull
    private long roleId;

    @NotNull
    private long organizationId;
}
