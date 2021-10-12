package com.epam.esm.web.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;
import java.util.Map;

public class ErrorResponse {
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp;
    private final Integer errorCode;
    private final Integer httpStatus;
    private final Map<String, Object> details;

    public ErrorResponse(LocalDateTime timestamp, Integer errorCode, Integer httpStatus, Map<String, Object> details) {
        this.timestamp = timestamp;
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.details = details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public Integer getHttpStatus() {
        return httpStatus;
    }

    public Map<String, Object> getDetails() {
        return details;
    }
}
