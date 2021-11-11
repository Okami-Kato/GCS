package com.epam.esm.entity;

import lombok.Builder;
import lombok.Getter;
import org.hibernate.Hibernate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "user_order")
@Getter
public class UserOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "user_id")
    private String userId;

    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "certificate_id")
    private Certificate certificate;

    @Column(name = "cost", nullable = false, updatable = false)
    private Integer cost;

    @Column(name = "timestamp", nullable = false, updatable = false)
    private Instant timestamp;

    protected UserOrder() {
    }

    @Builder
    public UserOrder(Integer id, String userId, Certificate certificate, Integer cost) {
        this.id = id;
        this.userId = userId;
        this.certificate = certificate;
        this.cost = cost;
    }

    @PrePersist
    private void toCreate() {
        timestamp = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserOrder that = (UserOrder) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        return "UserOrder{" +
                "id=" + id +
                ", userId=" + userId +
                ", certificate=" + certificate +
                ", cost=" + cost +
                ", timestamp=" + timestamp +
                '}';
    }
}