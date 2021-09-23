package com.epam.esm.dao;

import com.epam.esm.entity.Tag;

import java.util.List;

public interface TagDao extends Dao<Tag, Integer> {
    Tag getTheMostUsedTagOfUserWithTheMaximumCost();

    List<Tag> getAll(int pageNumber, int pageSize, Integer certificateId);
}
