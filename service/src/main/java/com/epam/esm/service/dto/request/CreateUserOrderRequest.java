package com.epam.esm.service.dto.request;

public class CreateUserOrderRequest {
    private int userId;
    private int certificateId;

    public CreateUserOrderRequest() {
    }

    public CreateUserOrderRequest(int userId, int certificateId) {
        this.userId = userId;
        this.certificateId = certificateId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(int certificateId) {
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
