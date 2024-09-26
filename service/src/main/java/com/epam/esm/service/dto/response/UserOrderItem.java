package com.epam.esm.service.dto.response;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.Objects;

@Relation(collectionRelation = "orders")
public class UserOrderItem extends RepresentationModel<UserOrderItem> {
    private Integer id;
    private Integer userId;
    private Integer certificateId;
    private Integer cost;

    public UserOrderItem() {
    }

    public UserOrderItem(Integer id, Integer userId, Integer certificateId, Integer cost) {
        this.id = id;
        this.userId = userId;
        this.certificateId = certificateId;
        this.cost = cost;
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
        UserOrderItem that = (UserOrderItem) o;
        return id.equals(that.id) && userId.equals(that.userId) && certificateId.equals(that.certificateId) && cost.equals(that.cost);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, certificateId, cost);
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
