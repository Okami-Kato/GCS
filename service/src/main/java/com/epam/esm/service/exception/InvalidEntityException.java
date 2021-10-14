package com.epam.esm.service.exception;

public class InvalidEntityException extends ServiceException {
    public InvalidEntityException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
