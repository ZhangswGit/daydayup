package com.connecttoweChat.error;


import com.connecttoweChat.constants.ErrorAlertMessages;

public class BadRequestException extends RuntimeException {
    public BadRequestException(ErrorAlertMessages errorAlertMessages){
        super(errorAlertMessages.getAlias());
    }
}
