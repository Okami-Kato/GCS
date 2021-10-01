package com.epam.esm.web.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends WebException{
    public BadRequestException(ErrorCode errorCode, String message) {
        super(errorCode, message, HttpStatus.BAD_REQUEST);
    }
}
