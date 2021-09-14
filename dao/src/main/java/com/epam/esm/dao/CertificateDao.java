package com.epam.esm.dao;

import com.epam.esm.entity.Certificate;

import java.util.List;

public interface CertificateDao extends Dao<Certificate, Integer> {
    void addTag(Integer certificateId, Integer tagId);

    boolean removeTag(Integer certificateId, Integer tagId);

    List<Certificate> getAllByTags(Integer... ids);
}
