package com.epam.esm.service.exception;

public class EntityExistsException extends ServiceException {
    public EntityExistsException(String message, ErrorCode errorCode) {
        super(String.format("Entity already exists (%s)", message), errorCode);
    }
}
