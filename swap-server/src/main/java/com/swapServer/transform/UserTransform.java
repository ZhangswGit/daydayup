package com.swapServer.transform;

import com.swapServer.bean.User;
import com.swapServer.model.response.UserModel;
import com.swapServer.netty.Model.ClientUserModel;
import org.springframework.stereotype.Component;

/**
 * @Data :  2021/3/3 19:21
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
@Component
public class UserTransform {

    public ClientUserModel toClientModel(User user) {
        return ClientUserModel.builder()
                .userId(user.getId())
                .userName(user.getNickName())
                .build();
    }

    public UserModel toModel(User user) {
        return UserModel.builder()
                .id(user.getId())
                .name(user.getNickName())
                .email(user.getEmail())
                .nickName(user.getNickName())
                .organizationId(user.getOrganizationId())
                .phone(user.getPhone())
                .roleId(user.getRoleId())
                .build();
    }
}
