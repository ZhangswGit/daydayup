package com.swapServer.transform;

import com.swapServer.bean.User;
import com.swapServer.netty.Model.UserModel;
import org.springframework.stereotype.Component;

/**
 * @Data :  2021/3/3 19:21
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
@Component
public class UserTransform {

    public UserModel toModel(User user) {
        return UserModel.builder()
                .userId(user.getId())
                .userName(user.getNickName())
                .build();
    }
}
