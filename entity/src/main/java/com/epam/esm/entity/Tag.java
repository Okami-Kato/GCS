package com.epam.esm.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tag")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @GenericGenerator(name = "native", strategy = "native")
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false, length = 25)
    private String name;

    @ManyToMany(mappedBy = "tags")
    private Set<Certificate> certificates;

    protected Tag() {
    }

    public Tag(String name) {
        this.name = name;
        this.certificates = new HashSet<>();
    }

    public Tag(String name, Set<Certificate> certificates) {
        this.name = name;
        this.certificates = new HashSet<>();
        for (Certificate certificate : certificates) {
            addCertificate(certificate);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return name.equals(tag.name);
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", name='" + name + '\'' +
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

    public Set<Certificate> getCertificates() {
        return new HashSet<>(certificates);
    }

    public void addCertificate(Certificate certificate) {
        if (certificates.contains(certificate))
            return;
        certificates.add(certificate);
        certificate.addTag(this);
    }

    public void removeCertificate(Certificate certificate) {
        if (!certificates.contains(certificate))
            return;
        certificates.remove(certificate);
        certificate.removeTag(this);
    }
}