package com.connecttoweChat.error;

import com.connecttoweChat.constants.ErrorAlertMessages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorHandle {

    @ExceptionHandler(value = { Exception.class })
    public ResponseEntity globalExceptionHandle(Exception e) {
        log.error(e.getMessage());
        e.printStackTrace();
        ResponseEntity responseBean = new ResponseEntity(ErrorAlertMessages.ServerError, HttpStatus.INTERNAL_SERVER_ERROR);
        return responseBean;
    }

    @ExceptionHandler(value = { BadRequestException.class })
    public ResponseEntity BadRequestExceptionHandle(Exception e) {
        ResponseEntity responseBean = new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        return responseBean;
    }
}
