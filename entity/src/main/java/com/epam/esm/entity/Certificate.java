package com.epam.esm.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "certificate")
@Getter
@Setter
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "description", nullable = false, length = 3000)
    private String description;

    @Column(name = "price")
    private Integer price;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "last_update_date", insertable = false)
    private Instant lastUpdateDate;

    @Column(name = "create_date", nullable = false, updatable = false)
    private Instant createDate;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(
            name = "certificate_tag",
            joinColumns = @JoinColumn(name = "certificate_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private final Set<Tag> tags = new HashSet<>();

    protected Certificate() {
    }

    @Builder
    public Certificate(Integer id, String name, String description, Integer price, Integer duration, Set<Tag> tags) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.duration = duration;
        if (tags != null) {
            tags.forEach(this::addTag);
        }
    }

    @PrePersist
    private void toCreate() {
        this.createDate = Instant.now();
    }

    @PreUpdate
    private void toUpdate() {
        this.lastUpdateDate = Instant.now();
    }

    public Set<Tag> getTags() {
        return new HashSet<>(tags);
    }

    public void addTag(Tag tag) {
        if (tags.contains(tag))
            return;
        tags.add(tag);
        tag.addCertificate(this);
    }

    public void removeTag(Tag tag) {
        if (!tags.contains(tag))
            return;
        tags.remove(tag);
        tag.removeCertificate(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Certificate that = (Certificate) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        return "Certificate{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", duration=" + duration +
                ", lastUpdateDate=" + lastUpdateDate +
                ", createDate=" + createDate +
                '}';
    }
}