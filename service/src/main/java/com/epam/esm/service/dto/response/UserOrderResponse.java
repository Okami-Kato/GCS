package com.epam.esm.service.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;
import java.util.Objects;

public class UserOrderResponse extends AbstractResponse {
    private UserResponse user;
    private CertificateItem certificate;
    private Integer cost;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public UserOrderResponse() {
    }

    public UserOrderResponse(Integer id, UserResponse user, CertificateItem certificate, Integer cost, LocalDateTime timestamp) {
        super(id);
        this.user = user;
        this.certificate = certificate;
        this.cost = cost;
        this.timestamp = timestamp;
    }

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
        return user.equals(that.user) && certificate.equals(that.certificate) && cost.equals(that.cost) && Objects.equals(timestamp, that.timestamp);
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
