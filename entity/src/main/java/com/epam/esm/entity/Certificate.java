package com.epam.esm.entity;

import org.hibernate.annotations.Type;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "certificate")
@NamedEntityGraph(name = "graph.certificate.tags", attributeNodes = @NamedAttributeNode("tags"))
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

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(
            name = "certificate_tag",
            joinColumns = @JoinColumn(name = "certificate_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private final Set<Tag> tags = new HashSet<>();

    protected Certificate() {
    }

    public Certificate(String name, String description, Integer price, Integer duration) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.duration = duration;
    }

    public Certificate(String name, String description, Integer price, Integer duration, Set<Tag> tags) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.duration = duration;
        for (Tag tag : tags) {
            addTag(tag);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Certificate that = (Certificate) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(description, that.description) && Objects.equals(price, that.price) && Objects.equals(duration, that.duration) && Objects.equals(lastUpdateDate, that.lastUpdateDate) && Objects.equals(createDate, that.createDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, price, duration, lastUpdateDate, createDate);
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Instant getLastUpdateDate() {
        return lastUpdateDate;
    }

    public Instant getCreateDate() {
        return createDate;
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
}