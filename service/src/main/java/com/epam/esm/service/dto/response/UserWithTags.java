package com.epam.esm.service.dto.response;

import org.springframework.hateoas.RepresentationModel;

import java.util.List;

public class UserWithTags extends RepresentationModel<UserWithTags> {
    private UserResponse user;
    private List<TagResponse> tags;

    public UserWithTags(UserResponse user, List<TagResponse> tags) {
        this.user = user;
        this.tags = tags;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }

    public List<TagResponse> getTags() {
        return tags;
    }

    public void setTags(List<TagResponse> tags) {
        this.tags = tags;
    }
}
