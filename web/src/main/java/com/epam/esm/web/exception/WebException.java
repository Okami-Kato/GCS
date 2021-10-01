package com.epam.esm.web.exception;

public class WebException extends RuntimeException{
    private final ErrorCode code;

    public WebException(ErrorCode code, String msg) {
        super(msg);
        this.code = code;
    }

    public ErrorCode getCode() {
        return code;
    }
}
