package com.epam.esm.service.dto.response;

import java.util.Objects;

public class UserOrderItem extends AbstractResponse {
    private Integer userId;
    private Integer certificateId;
    private Integer cost;

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

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UserOrderItem that = (UserOrderItem) o;
        return userId.equals(that.userId) && certificateId.equals(that.certificateId) && cost.equals(that.cost);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), userId, certificateId, cost);
    }

    @Override
    public String toString() {
        return "UserOrderListItem{" +
                "id=" + id +
                ", userId=" + userId +
                ", certificateId=" + certificateId +
                ", cost=" + cost +
                '}';
    }
}
