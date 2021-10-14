package com.epam.esm.dao.impl;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.Certificate_;
import com.epam.esm.entity.Tag;
import com.epam.esm.entity.Tag_;
import com.epam.esm.util.CertificateFilter;
import com.epam.esm.util.Sort;
import com.epam.esm.util.SortDirection;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Transactional
public class CertificateDaoImpl implements CertificateDao {
    private final String GET_ALL_CERTIFICATES_IDS = "SELECT c.id FROM Certificate c";
    private final String GET_ALL_CERTIFICATES_FROM_IDS = "SELECT c FROM Certificate c WHERE c.id in (:ids)";
    private final String GET_COUNT = "SELECT COUNT(c) FROM Certificate c";
    private final String SET_CERTIFICATE_NULL_IN_ORDERS_BY_CERTIFICATE_ID = "UPDATE UserOrder uo SET uo.certificate = NULL WHERE uo.certificate.id=:id";

    private final String GET_ALL_CERTIFICATES_IDS_BY_TAG_ID = "SELECT ct.certificate_id FROM certificate_tag ct WHERE ct.tag_id=:id";

    @PersistenceContext
    private EntityManager manager;

    /**
     * Retrieves certificate with given id.
     *
     * @param id id of certificate.
     * @return Optional with certificate, if it was found, otherwise an empty Optional.
     * @throws InvalidDataAccessApiUsageException if id is null.
     */
    @Override
    public Optional<Certificate> get(Integer id) {
        EntityGraph<?> graph = manager.getEntityGraph("graph.certificate.tags");
        Map<String, Object> hints = new HashMap<>();
        hints.put("javax.persistence.fetchgraph", graph);

        return Optional.ofNullable(manager.find(Certificate.class, id, hints));
    }

    /**
     * Retrieves all certificates.
     *
     * @param pageNumber number of page (starts from 1).
     * @param pageSize   size of page.
     * @return list of certificates.
     * @throws InvalidDataAccessApiUsageException if pageNumber < 1, or pageSize < 0.
     */
    @Override
    public List<Certificate> getAll(int pageNumber, int pageSize) {
        TypedQuery<Integer> idQuery = manager.createQuery(GET_ALL_CERTIFICATES_IDS, Integer.class);
        return getCertificatesByIdQuery(pageNumber, pageSize, idQuery);
    }

    /**
     * Retrieves all certificates, that match given filter.
     *
     * @param pageNumber number of page (starts from 1).
     * @param pageSize   size of page.
     * @param filter     filter to be applied.
     * @return list of found certificates.
     * @throws InvalidDataAccessApiUsageException if pageNumber < 1, or pageSize < 0, or if filter
     *                                            contains invalid sorting properties.
     */
    @Override
    public List<Certificate> findAllWithFilter(int pageNumber, int pageSize, CertificateFilter filter) {
        CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();
        CriteriaQuery<Integer> idQuery = criteriaBuilder.createQuery(Integer.class);
        Root<Certificate> root = idQuery.from(Certificate.class);

        idQuery.select(root.get(Certificate_.id));

        if (filter.getTagNames() != null) {
            SetJoin<Certificate, Tag> tags = root.join(Certificate_.tags);
            idQuery.where(tags.get(Tag_.name).in((Object[]) filter.getTagNames()));
            idQuery.having(criteriaBuilder.equal(criteriaBuilder.count(tags.get(Tag_.name)), filter.getTagNames().length));
            idQuery.groupBy(root.get(Certificate_.id));
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

    /**
     * Retrieves all certificates, that have tag with given id.
     *
     * @param pageNumber number of page (starts from 1).
     * @param pageSize   size of page.
     * @param tagId      id of tag.
     * @return list of found certificates.
     * @throws InvalidDataAccessApiUsageException if pageNumber < 1, or pageSize < 0.
     */
    @Override
    public List<Certificate> findAllByTagId(int pageNumber, int pageSize, int tagId) {
        Query idQuery = manager.createNativeQuery(GET_ALL_CERTIFICATES_IDS_BY_TAG_ID);
        idQuery.setParameter("id", tagId);
        return getCertificatesByIdQuery(pageNumber, pageSize, idQuery);
    }

    /**
     * Returns count of certificates.
     *
     * @return count of certificates.
     */
    @Override
    public long getCount() {
        return manager.createQuery(GET_COUNT, Long.class).getSingleResult();
    }

    /**
     * Creates certificate.
     *
     * @param certificate certificate to create.
     * @throws InvalidDataAccessApiUsageException if given certificate already exists, or if given certificate is null.
     * @throws DataIntegrityViolationException    if given certificate is invalid.
     */
    @Override
    public void create(Certificate certificate) {
        manager.persist(certificate);
    }

    /**
     * Updates certificate.
     *
     * @param certificate updated certificate.
     * @return updated certificate.
     * @throws InvalidDataAccessApiUsageException if given certificate is null.
     * @throws JpaObjectRetrievalFailureException if certificate with given id doesn't exist.
     * @throws DataIntegrityViolationException    if given certificate is invalid.
     */
    @Override
    public Certificate update(Certificate certificate) {
        Assert.notNull(certificate, "Certificate can't be null");
        if (!get(certificate.getId()).isPresent()) {
            throw new EntityNotFoundException(String.format("Certificate not found (id=%s)", certificate.getId()));
        }
        manager.merge(certificate);
        manager.flush();
        Certificate result = manager.find(Certificate.class, certificate.getId());
        manager.refresh(result);
        return result;
    }

    /**
     * Deletes certificate with given id.
     *
     * @param id id of certificate to delete.
     * @throws InvalidDataAccessApiUsageException if given id is null.
     * @throws JpaObjectRetrievalFailureException if certificate with given id doesn't exist.
     */
    @Override
    public void delete(Integer id) {
        Optional<Certificate> certificate = get(id);
        if (certificate.isPresent()) {
            setCertificateIdNullInOrders(id);
            manager.remove(certificate.get());
        } else {
            throw new EntityNotFoundException(String.format("Certificate not found (id=%s)", id));
        }
    }

    private List<Certificate> getCertificatesByIdQuery(int pageNumber, int pageSize, Query idQuery) {
        List<Integer> certificateIds = idQuery
                .setFirstResult((pageNumber - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();

        EntityGraph<?> graph = manager.getEntityGraph("graph.certificate.tags");

        TypedQuery<Certificate> certificateQuery = manager.createQuery(GET_ALL_CERTIFICATES_FROM_IDS, Certificate.class);
        return certificateQuery
                .setParameter("ids", certificateIds)
                .setHint("javax.persistence.fetchgraph", graph)
                .getResultList();
    }

    private void setCertificateIdNullInOrders(Integer certificateId) {
        manager.createQuery(SET_CERTIFICATE_NULL_IN_ORDERS_BY_CERTIFICATE_ID)
                .setParameter("id", certificateId)
                .executeUpdate();
    }
}
