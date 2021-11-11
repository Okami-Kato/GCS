package com.epam.esm.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToMany;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tag", indexes = {
        @Index(name = "name", columnList = "name", unique = true)
})
@Getter
@Setter
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false, unique = true, updatable = false, length = 25)
    private String name;

    @ManyToMany(mappedBy = "tags")
    private final Set<Certificate> certificates = new HashSet<>();

    protected Tag() {
    }

    @Builder
    public Tag(Integer id, String name, Set<Certificate> certificates) {
        this.id = id;
        this.name = name;
        if (certificates != null) {
            certificates.forEach(this::addCertificate);
        }
    }

    @PreRemove
    private void removeCertificateAssociations() {
        for (Iterator<Certificate> iterator = certificates.iterator(); iterator.hasNext(); ) {
            Certificate certificate = iterator.next();
            iterator.remove();
            certificate.removeTag(this);
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Tag tag = (Tag) o;
        return name != null && name.equals(tag.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}