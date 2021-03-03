package com.swapServer.netty.Model;

import lombok.Builder;
import lombok.Data;

/**
 * @Data :  2021/3/3 19:18
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
@Data
@Builder
public class UserModel {
    private long userId;
    private String userName;
}
