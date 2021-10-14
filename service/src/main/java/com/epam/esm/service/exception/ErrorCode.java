package com.epam.esm.service.exception;

public enum ErrorCode {
    CERTIFICATE_NOT_FOUND(10404),
    TAG_NOT_FOUND(20404),
    USER_NOT_FOUND(30404),
    USER_ORDER_NOT_FOUND(40404),
    INVALID_CERTIFICATE(10400),
    INVALID_TAG(20400),
    INVALID_USER(30400),
    INVALID_USER_ORDER(40400),
    TAG_EXISTS(20409),
    USER_EXISTS(30409);
    private final int value;

    ErrorCode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
