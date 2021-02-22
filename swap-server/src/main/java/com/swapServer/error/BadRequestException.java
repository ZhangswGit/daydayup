package com.swapServer.error;


import com.swapServer.constants.ErrorAlertMessages;

public class BadRequestException extends RuntimeException {
    public BadRequestException(ErrorAlertMessages errorAlertMessages){
        super(errorAlertMessages.getAlias());
    }
}
