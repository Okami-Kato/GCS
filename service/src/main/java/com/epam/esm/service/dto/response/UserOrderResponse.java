package com.epam.esm.service.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Objects;

public class UserOrderResponse extends AbstractResponse {
    private UserResponse user;
    private CertificateItem certificate;
    private Integer cost;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime timestamp;

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }

    public CertificateItem getCertificate() {
        return certificate;
    }

    public void setCertificate(CertificateItem certificate) {
        this.certificate = certificate;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UserOrderResponse that = (UserOrderResponse) o;
        return user.equals(that.user) && certificate.equals(that.certificate) && cost.equals(that.cost) && timestamp.equals(that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), user, certificate, cost, timestamp);
    }

    @Override
    public String toString() {
        return "UserOrderResponse{" +
                "id=" + id +
                ", user=" + user +
                ", certificate=" + certificate +
                ", cost=" + cost +
                ", timestamp=" + timestamp +
                '}';
    }
}
