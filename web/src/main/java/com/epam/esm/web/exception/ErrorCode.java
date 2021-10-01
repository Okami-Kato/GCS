package com.epam.esm.web.exception;

public enum ErrorCode {
    CERTIFICATE_NOT_FOUND(10404),
    TAG_NOT_FOUND(20404),
    USER_NOT_FOUND(30404),
    USER_ORDER_NOT_FOUND(40404),
    CERTIFICATE_BAD_REQUEST(10400),
    TAG_BAD_REQUEST(20400),
    USER_BAD_REQUEST(30400),
    USER_ORDER_BAD_REQUEST(40400);
    private final int value;

    ErrorCode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
