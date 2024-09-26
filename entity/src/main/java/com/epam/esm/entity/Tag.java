package com.epam.esm.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tag")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false, length = 25, unique = true)
    private String name;

    @ManyToMany(mappedBy = "tags")
    private Set<Certificate> certificates = new HashSet<>();

    @PreRemove
    private void removeCertificateAssociations() {
        for (Iterator<Certificate> iterator = certificates.iterator(); iterator.hasNext(); ) {
            Certificate certificate = iterator.next();
            iterator.remove();
            certificate.removeTag(this);
        }
    }

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(id, tag.id) && name.equals(tag.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
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