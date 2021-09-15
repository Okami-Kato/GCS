package com.epam.esm.entity;

import org.hibernate.annotations.GenericGenerator;

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
import javax.persistence.Table;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "certificate")
@NamedEntityGraph(name = "graph.certificate.tags", attributeNodes = @NamedAttributeNode("tags"))
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @GenericGenerator(name = "native", strategy = "native")
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Lob
    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "price")
    private Integer price;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "last_update_date", nullable = false)
    private Instant lastUpdateDate;

    @Column(name = "create_date", nullable = false)
    private Instant createDate;

    @ManyToMany
    @JoinTable(
            name = "certificate_tag",
            joinColumns = @JoinColumn(name = "certificate_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags;

    protected Certificate() {
    }

    public Certificate(String name, String description, Integer price, Integer duration, Instant lastUpdateDate, Instant createDate, Set<Tag> tags) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.duration = duration;
        this.lastUpdateDate = lastUpdateDate;
        this.createDate = createDate;
        this.tags = tags;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, price, duration, lastUpdateDate, createDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Certificate that = (Certificate) o;
        return Objects.equals(id, that.id) && name.equals(that.name) && description.equals(that.description) && price.equals(that.price) && duration.equals(that.duration) && lastUpdateDate.equals(that.lastUpdateDate) && createDate.equals(that.createDate);
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
                ", tags=" + tags +
                '}';
    }

    public Instant getCreateDate() {
        return createDate;
    }

    public Instant getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Instant lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}