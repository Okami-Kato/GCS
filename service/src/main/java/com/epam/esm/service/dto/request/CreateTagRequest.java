package com.epam.esm.service.dto.request;

import java.util.Objects;

public class CreateTagRequest {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateTagRequest that = (CreateTagRequest) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "CreateTagRequest{" +
                "name='" + name + '\'' +
                '}';
    }
}
