package com.epam.esm.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public class CreateTagRequest {
    @NotBlank(message = "Tag name can't be blank")
    @Size(min = 3, max = 25, message = "Tag name must be {min}-{max} characters long.")
    @Pattern(regexp = "^[a-zA-Z\\s]$", message = "Tag name can contain only letters.")
    private String name;

    public CreateTagRequest() {
    }

    public CreateTagRequest(String name) {
        this.name = name;
    }

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
