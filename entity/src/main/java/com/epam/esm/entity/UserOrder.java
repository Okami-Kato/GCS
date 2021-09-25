package com.epam.esm.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.criteria.CriteriaBuilder;
import java.time.Instant;

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

    public UserOrder(User user, Certificate certificate, Integer cost, Instant timestamp) {
        this.user = user;
        this.certificate = certificate;
        this.cost = cost;
        this.timestamp = timestamp;
    }

    @PrePersist
    private void toCreate(){
        timestamp = Instant.now();
        cost = certificate.getPrice();
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
        if (this.user != null){
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
}