package com.epam.esm.service.exception;

public class EntityNotFoundException extends ServiceException {
    public EntityNotFoundException(String message, ErrorCode errorCode) {
        super(String.format("Entity not found (%s)", message), errorCode);
    }
}
