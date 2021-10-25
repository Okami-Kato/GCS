package com.epam.esm.dao;

import com.epam.esm.entity.Certificate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

public interface CertificateDao extends CustomRepository<Certificate, Integer>, JpaSpecificationExecutor<Certificate> {
    Page<Certificate> findAllByTagsId(int tagId, Pageable pageable);
}
