package com.epam.esm.service.dto.request;

import java.util.Objects;

public class CreateUserOrderRequest {
    private Integer userId;
    private Integer certificateId;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(Integer certificateId) {
        this.certificateId = certificateId;
    }

    @Override
    public String toString() {
        return "CreateUserOrderRequest{" +
                "userId=" + userId +
                ", certificateId=" + certificateId +
                '}';
    }
}
