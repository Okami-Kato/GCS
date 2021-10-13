package com.epam.esm.dao;

import com.epam.esm.entity.Certificate;
import com.epam.esm.util.CertificateFilter;

import java.util.List;

public interface CertificateDao extends Dao<Certificate, Integer> {
    List<Certificate> findAllWithFilter(int pageNumber, int pageSize, CertificateFilter filter);

    List<Certificate> findAllByTagId(int pageNumber, int pageSize, int tagId);
}
