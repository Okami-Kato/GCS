package com.epam.esm.web.exception;

import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends WebException{
    public EntityNotFoundException(ErrorCode errorCode, int id) {
        super(errorCode, String.format("Entity not found (id=%s)", id), HttpStatus.NOT_FOUND);
    }
}
