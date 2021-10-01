package com.epam.esm.web.exception;

import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends WebException{
    public EntityNotFoundException(ErrorCode errorCode, String msg) {
        super(errorCode, String.format("Entity not found (%s)", msg), HttpStatus.NOT_FOUND);
    }
}
