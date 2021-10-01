package com.epam.esm.web.exception;

public class BadRequestException extends WebException{
    public BadRequestException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
