package com.epam.esm.dao;

import com.epam.esm.entity.Tag;
import com.epam.esm.entity.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TagDao extends Dao<Tag, Integer> {
    List<Tag> findAllByCertificateId(int pageNumber, int pageSize, int certificateId);

    Map<User, List<Tag>> getTheMostUsedTagsOfUsersWithTheHighestCost();

    Optional<Tag> get(String name);
}
