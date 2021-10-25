package com.epam.esm.service.dto.response;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.List;
import java.util.Objects;

@Relation(collectionRelation = "usersAndTags")
public class UserWithTags extends RepresentationModel<UserWithTags> {
    private Integer userId;
    private List<TagResponse> tags;

    public UserWithTags() {
    }

    public UserWithTags(Integer userId, List<TagResponse> tags) {
        this.userId = userId;
        this.tags = tags;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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
        return userId.equals(that.userId) && tags.equals(that.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, tags);
    }
}
