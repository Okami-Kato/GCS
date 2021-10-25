package com.epam.esm.dao;

import com.epam.esm.entity.Certificate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CertificateDao extends JpaRepository<Certificate, Integer>, JpaSpecificationExecutor<Certificate> {
    Page<Certificate> findAllByTagsId(int tagId, Pageable pageable);
}
