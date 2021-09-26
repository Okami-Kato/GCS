package com.epam.esm.service.dto.request;

import java.util.Set;

public class CreateCertificateRequest {
    private String name;
    private String description;
    private Integer price;
    private Integer duration;

    private Set<String> tagNames;

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

    public Set<String> getTagNames() {
        return tagNames;
    }

    public void setTagNames(Set<String> tagNames) {
        this.tagNames = tagNames;
    }

    @Override
    public String toString() {
        return "CreateCertificateRequest{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", cost=" + price +
                ", duration=" + duration +
                ", tagNames=" + tagNames +
                '}';
    }
}
