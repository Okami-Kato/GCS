package com.epam.esm.service.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;
import java.util.Objects;

@Relation(collectionRelation = "orders")
public class UserOrderResponse extends RepresentationModel<UserOrderResponse> {
    private Integer id;
    private Integer userId;
    private CertificateItem certificate;
    private Integer cost;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public UserOrderResponse() {
    }

    public UserOrderResponse(Integer id, Integer userId, CertificateItem certificate, Integer cost, LocalDateTime timestamp) {
        this.id = id;
        this.userId = userId;
        this.certificate = certificate;
        this.cost = cost;
        this.timestamp = timestamp;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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
        UserOrderResponse that = (UserOrderResponse) o;
        return id.equals(that.id) && userId.equals(that.userId) && certificate.equals(that.certificate) && cost.equals(that.cost) && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, certificate, cost, timestamp);
    }

    @Override
    public String toString() {
        return "UserOrderResponse{" +
                "id=" + id +
                ", user=" + userId +
                ", certificate=" + certificate +
                ", cost=" + cost +
                ", timestamp=" + timestamp +
                '}';
    }
}
