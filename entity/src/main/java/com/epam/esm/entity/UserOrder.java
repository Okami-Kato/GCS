package com.epam.esm.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.Instant;

@Table(name = "user_order", indexes = {
        @Index(name = "user_id", columnList = "user_id, certificate_id", unique = true)
})
@Entity
public class UserOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @GenericGenerator(name = "native", strategy = "native")
    @Column(name = "id", nullable = false)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "certificate_id")
    private Certificate certificate;
    @Column(name = "cost")
    private Integer cost;
    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    protected UserOrder() {
    }

    public UserOrder(User user, Certificate certificate, Integer cost, Instant timestamp) {
        this.user = user;
        this.certificate = certificate;
        this.cost = cost;
        this.timestamp = timestamp;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public Integer getCost() {
        return cost;
    }

    public User getUser() {
        return user;
    }

    public Certificate getCertificate() {
        return certificate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}