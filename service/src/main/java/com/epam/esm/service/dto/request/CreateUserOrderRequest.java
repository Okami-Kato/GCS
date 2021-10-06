package com.epam.esm.service.dto.request;

import javax.validation.constraints.NotNull;

public class CreateUserOrderRequest {
    @NotNull(message = "User id can't be null.")
    private Integer userId;

    @NotNull(message = "Certificate id can't be null.")
    private Integer certificateId;

    public CreateUserOrderRequest() {
    }

    public CreateUserOrderRequest(Integer userId, Integer certificateId) {
        this.userId = userId;
        this.certificateId = certificateId;
    }

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
