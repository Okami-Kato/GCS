package com.epam.esm.dao.impl;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.entity.Certificate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class CertificateDaoImpl implements CertificateDao {
    @PersistenceContext
    private EntityManager manager;

    @Override
    public void addTag(Integer certificateId, Integer tagId) {

    }

    @Override
    public boolean removeTag(Integer certificateId, Integer tagId) {
        return false;
    }

    @Override
    public List<Certificate> getAllByTags(Integer... ids) {
        return null;
    }

    @Override
    public Optional<Certificate> get(Integer id) {
        return Optional.ofNullable(manager.find(Certificate.class, id));
    }

    @Override
    public List<Certificate> getAll(int pageNumber, int pageSize) {
        return null;
    }

    @Override
    public void create(Certificate certificate) {
        manager.persist(certificate);
    }

    @Override
    public void update(Certificate certificate) {
        manager.merge(certificate);
    }

    @Override
    public void delete(Integer id) {
        Optional<Certificate> certificate = get(id);
        if (certificate.isPresent()) {
            manager.remove(certificate.get());
        } else {
            throw new IllegalArgumentException(String.format("Entity wasn't found (%s)", "id=" + id));
        }
    }
}
