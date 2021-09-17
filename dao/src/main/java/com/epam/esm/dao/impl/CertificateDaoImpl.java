package com.epam.esm.dao.impl;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.Tag;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Transactional
public class CertificateDaoImpl implements CertificateDao {
    @PersistenceContext
    private EntityManager manager;

    @Override
    public void addTag(Integer certificateId, Integer tagId) {
        Certificate certificate = manager.getReference(Certificate.class, certificateId);
        Tag tag = manager.find(Tag.class, tagId);
        if (tag != null) {
            certificate.addTag(tag);
        } else {
            //todo throw exception
        }
    }

    @Override
    public void removeTag(Integer certificateId, Integer tagId) {
        Certificate certificate = manager.getReference(Certificate.class, certificateId);
        Tag tag = manager.find(Tag.class, tagId);
        if (tag != null) {
            certificate.removeTag(tag);
        } else {
            //todo throw exception
        }
    }

    @Override
    public List<Certificate> getAll(int pageNumber, int pageSize, Integer... tagIds) {
        TypedQuery<Integer> idQuery = manager.createQuery("SELECT c.id FROM Certificate c JOIN c.tags t WHERE t.id in (:ids) order by c.id", Integer.class);
        List<Integer> certificateIds = idQuery
                .setParameter("ids", Arrays.asList(tagIds))
                .setFirstResult((pageNumber - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();

        EntityGraph<?> graph = manager.getEntityGraph("graph.certificate.tags");

        TypedQuery<Certificate> certificateQuery = manager.createQuery("SELECT c FROM Certificate c WHERE c.id in (:ids)", Certificate.class);
        return certificateQuery
                .setParameter("ids", certificateIds)
                .setHint("javax.persistence.fetchgraph", graph)
                .getResultList();
    }

    @Override
    public int getCount() {
        return manager.createQuery("SELECT COUNT(c) FROM Certificate c", Integer.class).getSingleResult();
    }

    @Override
    public Optional<Certificate> get(Integer id) {
        EntityGraph<?> graph = manager.getEntityGraph("graph.certificate.tags");
        Map<String, Object> hints = new HashMap<>();
        hints.put("javax.persistence.fetchgraph", graph);

        return Optional.ofNullable(manager.find(Certificate.class, id, hints));
    }

    @Override
    public List<Certificate> getAll(int pageNumber, int pageSize) {
        TypedQuery<Integer> idQuery = manager.createQuery("SELECT c.id FROM Certificate c", Integer.class);
        List<Integer> certificateIds = idQuery
                .setFirstResult((pageNumber - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();

        EntityGraph<?> graph = manager.getEntityGraph("graph.certificate.tags");

        TypedQuery<Certificate> certificateQuery = manager.createQuery("SELECT c FROM Certificate c WHERE c.id in (:ids)", Certificate.class);
        return certificateQuery
                .setParameter("ids", certificateIds)
                .setHint("javax.persistence.fetchgraph", graph)
                .getResultList();
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
