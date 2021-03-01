package com.swapServer.config.mybaits;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.swapServer.utils.SecurityUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        Object createDate = metaObject.getValue("createDate");
        if (ObjectUtils.isEmpty(createDate)){
            metaObject.setValue("createDate", Instant.now());
        }
        String currentUser = SecurityUtils.currentUser();
        Object createUser = metaObject.getValue("createUser");
        if (ObjectUtils.isEmpty(createUser)){
            metaObject.setValue("createUser", currentUser);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Object createUser = metaObject.getValue("updateDate");
        if (ObjectUtils.isEmpty(createUser)){
            metaObject.setValue("updateDate", Instant.now());
        }
        String currentUser = SecurityUtils.currentUser();
        Object updateUser = metaObject.getValue("updateUser");
        if (ObjectUtils.isEmpty(updateUser)){
            metaObject.setValue("updateUser", currentUser);
        }
    }
}
