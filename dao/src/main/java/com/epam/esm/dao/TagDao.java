package com.epam.esm.dao;

import com.epam.esm.entity.Tag;

import java.util.List;

public interface TagDao extends Dao<Tag, Integer> {
    List<Tag> getAllByCertificateId(Integer certificateId);

    boolean nameExists(String name);
}
