package com.epam.esm.service.dto.response;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.List;
import java.util.Objects;

@Relation(collectionRelation = "usersAndTags")
public class UserWithTags extends RepresentationModel<UserWithTags> {
    private UserResponse user;
    private List<TagResponse> tags;

    public UserWithTags() {
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserWithTags that = (UserWithTags) o;
        return user.equals(that.user) && tags.equals(that.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, tags);
    }
}
