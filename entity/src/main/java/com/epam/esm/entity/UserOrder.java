package com.epam.esm.entity;

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
import java.util.Objects;

@Entity
@Table(name = "user_order")
public class UserOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
        setCertificate(certificate);
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

    void setUser(User user) {
        if (this.user == null)
            return;
        this.user = user;
        user.addOrder(this);
    }

    public Certificate getCertificate() {
        return certificate;
    }

    void setCertificate(Certificate certificate) {
        if (this.certificate == null)
            return;
        this.certificate = certificate;
        certificate.addOrder(this);
    }

    void removeCertificate() {
        if (this.certificate == null)
            return;
        certificate.removeOrder(this);
        this.certificate = null;
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
        return Objects.equals(id, userOrder.id) && Objects.equals(user, userOrder.user) && Objects.equals(certificate, userOrder.certificate) && Objects.equals(cost, userOrder.cost) && Objects.equals(timestamp, userOrder.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, certificate, cost, timestamp);
    }

    @Override
    public String toString() {
        return "UserOrder{" +
                "id=" + id +
                ", user=" + user +
                ", certificate=" + certificate +
                ", cost=" + cost +
                ", timestamp=" + timestamp +
                '}';
    }
}