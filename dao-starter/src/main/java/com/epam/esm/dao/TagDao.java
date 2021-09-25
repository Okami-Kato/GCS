package com.epam.esm.dao;

import com.epam.esm.entity.Tag;

import java.util.List;
import java.util.Optional;

public interface TagDao extends Dao<Tag, Integer> {
    List<Tag> getAll(int pageNumber, int pageSize, Integer certificateId);

    Tag getTheMostUsedTagOfUserWithTheMaximumCost();

    Optional<Tag> get(String name);
}
