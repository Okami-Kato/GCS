package com.epam.esm.service.dto.response;

import org.springframework.hateoas.RepresentationModel;

import java.util.Objects;
import java.util.Set;

public class CertificateItem extends RepresentationModel<CertificateItem> {
    private Integer id;
    private String name;
    private Integer price;

    private Set<TagResponse> tags;

    public CertificateItem() {
    }

    public CertificateItem(Integer id, String name, Integer price, Set<TagResponse> tags) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.tags = tags;
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

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Set<TagResponse> getTags() {
        return tags;
    }

    public void setTags(Set<TagResponse> tags) {
        this.tags = tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CertificateItem that = (CertificateItem) o;
        return id.equals(that.id) && name.equals(that.name) && price.equals(that.price) && Objects.equals(tags, that.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, name, price, tags);
    }

    @Override
    public String toString() {
        return "CertificateDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", tags=" + tags +
                '}';
    }
}
