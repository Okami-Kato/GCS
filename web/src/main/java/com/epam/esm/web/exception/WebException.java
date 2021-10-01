package com.epam.esm.web.exception;

import org.springframework.http.HttpStatus;

public class WebException extends RuntimeException{
    private final ErrorCode code;
    private final HttpStatus status;

    public WebException(ErrorCode code, String msg, HttpStatus status) {
        super(msg);
        this.code = code;
        this.status = status;
    }

    public ErrorCode getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
