package com.swapServer.error;

import com.swapServer.constants.ErrorAlertMessages;

/**
 * @Data :  2021/3/1 16:11
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
public class UserOfflineException extends RuntimeException {
    public UserOfflineException(ErrorAlertMessages errorAlertMessages){
        super(errorAlertMessages.getAlias());
    }
}
