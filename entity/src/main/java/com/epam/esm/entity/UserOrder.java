package com.epam.esm.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "user_order", indexes = {
        @Index(name = "user_id", columnList = "user_id, certificate_id", unique = true)
})
public class UserOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @GenericGenerator(name = "native", strategy = "native")
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "certificate_id", nullable = false)
    private Certificate certificate;

    @Column(name = "cost", nullable = false, updatable = false)
    private Integer cost;

    @Column(name = "timestamp", nullable = false, updatable = false)
    private Instant timestamp;

    protected UserOrder() {
    }

    public UserOrder(User user, Certificate certificate, Integer cost) {
        setUser(user);
        this.certificate = certificate;
        this.cost = cost;
    }

    @PrePersist
    private void toCreate() {
        timestamp = Instant.now();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        if (this.user != null) {
            return;
        }
        this.user = user;
        user.addOrder(this);
    }

    public Certificate getCertificate() {
        return certificate;
    }

    public Integer getCost() {
        return cost;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserOrder userOrder = (UserOrder) o;
        return Objects.equals(id, userOrder.id) && user.equals(userOrder.user) && certificate.equals(userOrder.certificate) && cost.equals(userOrder.cost) && Objects.equals(timestamp, userOrder.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, certificate, cost, timestamp);
    }
}