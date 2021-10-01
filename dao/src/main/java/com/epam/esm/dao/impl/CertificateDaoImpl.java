package com.epam.esm.dao.impl;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.Certificate_;
import com.epam.esm.util.CertificateFilter;
import com.epam.esm.util.Sort;
import com.epam.esm.util.SortDirection;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Transactional
public class CertificateDaoImpl implements CertificateDao {
    @PersistenceContext
    private EntityManager manager;

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
        return getAllFromIds(certificateIds);
    }

    @Override
    public List<Certificate> getAll(int pageNumber, int pageSize, CertificateFilter filter) {
        CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();
        CriteriaQuery<Integer> idQuery = criteriaBuilder.createQuery(Integer.class);
        Root<Certificate> root = idQuery.from(Certificate.class);

        idQuery.select(root.get(Certificate_.id));

        if (filter.getTagIds() != null) {
            idQuery.where(root.get(Certificate_.id).in(getCertificateIdsFromTagIds(Arrays.asList(filter.getTagIds()))));
        }
        if (filter.getNamePart() != null) {
            idQuery.where(criteriaBuilder.like(
                    root.get(Certificate_.name),
                    "%" + filter.getNamePart() + "%"));
        }
        if (filter.getDescriptionPart() != null) {
            idQuery.where(criteriaBuilder.like(
                    root.get(Certificate_.description),
                    "%" + filter.getDescriptionPart() + "%"));
        }
        List<Order> orderList = new LinkedList<>();
        if (filter.getSort() != null) {
            for (Sort.Order order : filter.getSort().getOrders()) {
                if (order.getDirection().equals(SortDirection.DESC)) {
                    orderList.add(criteriaBuilder.desc(root.get(order.getField())));
                } else {
                    orderList.add(criteriaBuilder.asc(root.get(order.getField())));
                }
            }
        }
        idQuery.orderBy(orderList);
        List<Integer> certificateIds = manager.createQuery(idQuery)
                .setFirstResult((pageNumber - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();

        CriteriaQuery<Certificate> finalQuery = criteriaBuilder.createQuery(Certificate.class);
        root = finalQuery.from(Certificate.class);

        finalQuery.select(root);
        finalQuery.where(root.get(Certificate_.id).in(certificateIds));
        finalQuery.orderBy(orderList);
        EntityGraph<?> graph = manager.getEntityGraph("graph.certificate.tags");
        TypedQuery<Certificate> query = manager.createQuery(finalQuery);
        query.setHint("javax.persistence.fetchgraph", graph);
        return query.getResultList();
    }

    @Override
    public long getCount() {
        return manager.createQuery("SELECT COUNT(c) FROM Certificate c", Long.class).getSingleResult();
    }

    @Override
    public void create(Certificate certificate) {
        manager.persist(certificate);
    }

    @Override
    public Certificate update(Certificate certificate) {
        if (certificate == null){
            throw new IllegalArgumentException("Certificate can't be null");
        }
        if (!get(certificate.getId()).isPresent()) {
            throw new IllegalArgumentException(String.format("Entity doesn't exist (%s)", "id=" + certificate.getId()));
        }
        manager.merge(certificate);
        manager.flush();
        Certificate result = manager.find(Certificate.class, certificate.getId());
        manager.refresh(result);
        return result;
    }

    @Override
    public void delete(Integer id) {
        Optional<Certificate> certificate = get(id);
        if (certificate.isPresent()) {
            manager.remove(certificate.get());
        } else {
            throw new InvalidDataAccessApiUsageException(String.format("Entity wasn't found (%s)", "id=" + id));
        }
    }

    private List<Integer> getCertificateIdsFromTagIds(List<Integer> tagIds) {
        return manager.createQuery("SELECT c.id FROM Certificate c LEFT JOIN c.tags t WHERE t.id IN (:ids) GROUP BY c HAVING COUNT(t)=:idsCount", Integer.class)
                .setParameter("ids", tagIds)
                .setParameter("idsCount", (long) tagIds.size())
                .getResultList();
    }

    private List<Certificate> getAllFromIds(List<Integer> ids) {
        EntityGraph<?> graph = manager.getEntityGraph("graph.certificate.tags");

        TypedQuery<Certificate> certificateQuery = manager.createQuery("SELECT c FROM Certificate c WHERE c.id in (:ids) ORDER BY c.id", Certificate.class);
        return certificateQuery
                .setParameter("ids", ids)
                .setHint("javax.persistence.fetchgraph", graph)
                .getResultList();
    }
}
