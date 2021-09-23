package com.epam.esm.dao.impl;

import com.epam.esm.dao.TagDao;
import com.epam.esm.entity.Tag;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Transactional
public class TagDaoImpl implements TagDao {
    private final String GET_THE_MOST_USED_TAG_OF_USER_WITH_THE_HIGHEST_COST =
            "WITH user_cost_sum AS (" +
                    "    SELECT user_id, SUM(cost) cost_sum" +
                    "    FROM user_order" +
                    "    GROUP BY user_id" +
                    ")," +
                    "     tag_certificate_count AS (" +
                    "         SELECT tag_id, COUNT(certificate_id) certificate_count" +
                    "         FROM certificate_tag" +
                    "         WHERE certificate_id IN (" +
                    "             SELECT certificate_id" +
                    "             FROM user_order" +
                    "             WHERE user_id = (" +
                    "                 SELECT user_id" +
                    "                 FROM user_cost_sum" +
                    "                 WHERE cost_sum = (" +
                    "                     SELECT MAX(cost_sum)" +
                    "                     FROM user_cost_sum" +
                    "                 )" +
                    "             )" +
                    "         )" +
                    "         GROUP BY tag_id" +
                    "     )" +
                    "SELECT t.id, t.name " +
                    "FROM tag t" +
                    "         INNER JOIN (" +
                    "    SELECT tag_id" +
                    "    FROM tag_certificate_count" +
                    "    WHERE certificate_count = (" +
                    "        SELECT MAX(certificate_count)" +
                    "        FROM tag_certificate_count" +
                    "    )" +
                    ") t_id on t.id = t_id.tag_id";

    @PersistenceContext
    private EntityManager manager;

    @Override
    public Optional<Tag> get(Integer id) {
        EntityGraph<?> graph = manager.getEntityGraph("graph.tag.certificates");
        Map<String, Object> hints = new HashMap<>();
        hints.put("javax.persistence.fetchgraph", graph);

        return Optional.ofNullable(manager.find(Tag.class, id, hints));
    }

    @Override
    public List<Tag> getAll(int pageNumber, int pageSize) {
        TypedQuery<Integer> idQuery = manager.createQuery("SELECT t.id FROM Tag t ORDER BY t.id", Integer.class);
        List<Integer> tagIds = idQuery
                .setFirstResult((pageNumber - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();

        EntityGraph<?> graph = manager.getEntityGraph("graph.tag.certificates");

        TypedQuery<Tag> tagQuery = manager.createQuery("SELECT t FROM Tag t WHERE t.id in (:ids)", Tag.class);
        return tagQuery
                .setParameter("ids", tagIds)
                .setHint("javax.persistence.fetchgraph", graph)
                .getResultList();
    }

    @Override
    public long getCount() {
        return manager.createQuery("SELECT COUNT(t) FROM Tag t", Long.class).getSingleResult();
    }

    @Override
    public void create(Tag tag) {
        manager.persist(tag);
    }

    @Override
    public void update(Tag tag) {
        throw new UnsupportedOperationException("Method update() isn't supported in TagDaoImpl");
    }

    @Override
    public void delete(Integer id) {
        Optional<Tag> tag = get(id);
        if (tag.isPresent()) {
            manager.remove(tag.get());
        } else {
            throw new IllegalArgumentException(String.format("Entity wasn't found (%s)", "id=" + id));
        }
    }

    @Override
    public Tag getTheMostUsedTagOfUserWithTheMaximumCost() {
        Query nativeQuery = manager.createNativeQuery(GET_THE_MOST_USED_TAG_OF_USER_WITH_THE_HIGHEST_COST, Tag.class);
        return (Tag) nativeQuery.getSingleResult();
    }

    @Override
    public List<Tag> getAll(int pageNumber, int pageSize, Integer certificateId) {
        TypedQuery<Integer> idQuery = manager.createQuery("SELECT t.id FROM Tag t JOIN t.certificates c WHERE c.id=:id ORDER BY t.id", Integer.class);
        List<Integer> tagIds = idQuery
                .setParameter("id", certificateId)
                .setFirstResult((pageNumber - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();

        EntityGraph<?> graph = manager.getEntityGraph("graph.tag.certificates");

        TypedQuery<Tag> tagQuery = manager.createQuery("SELECT t FROM Tag t WHERE t.id in (:ids)", Tag.class);
        return tagQuery
                .setParameter("ids", tagIds)
                .setHint("javax.persistence.fetchgraph", graph)
                .getResultList();
    }
}
