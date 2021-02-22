package com.swapServer.model.request;

import lombok.Builder;

/**
 * @Data : 2020/12/25
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
@Builder
public class QueryUserRequest {

    private String nickName;

    private String phone;

    private String Email;
}