package com.epam.esm.dao.impl;

import com.epam.esm.dao.TagDao;
import com.epam.esm.entity.Tag;
import com.epam.esm.entity.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
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
    private final String GET_USERS_WITH_HIGHEST_COST =
            "WITH user_cost_sum AS (" +
                    "    SELECT user_id, SUM(cost) cost_sum" +
                    "    FROM user_order" +
                    "    GROUP BY user_id" +
                    ")" +
                    "SELECT u.id, first_name, last_name, login, password " +
                    "FROM user u " +
                    "         INNER JOIN (" +
                    "    SELECT user_id" +
                    "    FROM user_cost_sum" +
                    "    WHERE cost_sum = (SELECT MAX(cost_sum) FROM user_cost_sum)" +
                    ") AS ui ON u.id = ui.user_id";

    private final String GET_THE_MOST_USED_TAG_OF_USER =
            "WITH tag_certificate_count AS (" +
                    "    SELECT tag_id, COUNT(certificate_id) certificate_count" +
                    "    FROM CERTIFICATE_TAG" +
                    "    WHERE certificate_id in (" +
                    "        SELECT certificate_id" +
                    "        FROM USER_ORDER" +
                    "        WHERE user_id = :userId" +
                    "        )" +
                    "    GROUP BY tag_id" +
                    ")" +
                    "SELECT t.id, t.name " +
                    "FROM tag t " +
                    "         INNER JOIN (SELECT tag_id" +
                    "                     FROM tag_certificate_count" +
                    "                     WHERE certificate_count = (" +
                    "                         SELECT MAX(certificate_count)" +
                    "                         FROM tag_certificate_count)" +
                    ") as ti on t.id = ti.tag_id";

    private final String GET_TAG_BY_NAME = "SELECT t FROM Tag t WHERE t.name=:name";
    private final String GET_ALL_TAGS = "SELECT t FROM Tag t";
    private final String GET_ALL_TAGS_BY_CERTIFICATE_ID = "SELECT t FROM Tag t JOIN t.certificates c WHERE c.id=:id";
    private final String GET_COUNT = "SELECT COUNT(t) FROM Tag t";

    @PersistenceContext
    private EntityManager manager;

    /**
     * Retrieves tag with given id.
     *
     * @param id id of tag.
     * @return Optional with tag, if it was found, otherwise an empty Optional.
     * @throws InvalidDataAccessApiUsageException if id is null.
     */
    @Override
    public Optional<Tag> find(Integer id) {
        return Optional.ofNullable(manager.find(Tag.class, id));
    }

    /**
     * Retrieves tag with given name.
     *
     * @param name name of tag.
     * @return Optional with tag, if it was found, otherwise an empty Optional.
     * @throws InvalidDataAccessApiUsageException if name is null.
     */
    @Override
    public Optional<Tag> get(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Tag name can't be null");
        }
        TypedQuery<Tag> query = manager.createQuery(GET_TAG_BY_NAME, Tag.class);
        query.setParameter("name", name);
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * Retrieves the most widely used tags of users with the highest cost of all orders.
     *
     * @return found users and corresponding tags.
     */
    @Override
    public Map<User, List<Tag>> getTheMostUsedTagsOfUsersWithTheHighestCost() {
        Map<User, List<Tag>> result = new HashMap<>();
        Query userQuery = manager.createNativeQuery(GET_USERS_WITH_HIGHEST_COST, User.class);
        List<User> userList = (List<User>) userQuery.getResultList();
        for (User user : userList) {
            Query tagQuery = manager.createNativeQuery(GET_THE_MOST_USED_TAG_OF_USER, Tag.class);
            List<Tag> tagList = (List<Tag>) tagQuery.setParameter("userId", user.getId()).getResultList();
            result.put(user, tagList);
        }
        return result;
    }

    /**
     * Retrieves all tags.
     *
     * @param pageNumber number of page (starts from 1).
     * @param pageSize   size of page.
     * @return list of tags.
     * @throws InvalidDataAccessApiUsageException if pageNumber < 1, or pageSize < 0.
     */
    @Override
    public List<Tag> findAll(int pageNumber, int pageSize) {
        TypedQuery<Tag> query = manager.createQuery(GET_ALL_TAGS, Tag.class);
        return query.setFirstResult((pageNumber - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public List<Tag> findAllByCertificateId(int pageNumber, int pageSize, int certificateId) {
        TypedQuery<Tag> query = manager.createQuery(GET_ALL_TAGS_BY_CERTIFICATE_ID, Tag.class);
        return query.setParameter("id", certificateId)
                .setFirstResult((pageNumber - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    /**
     * Returns count of tags.
     *
     * @return count of tags.
     */
    @Override
    public long getCount() {
        return manager.createQuery(GET_COUNT, Long.class).getSingleResult();
    }

    /**
     * Creates tag.
     *
     * @param tag tag to create.
     * @throws InvalidDataAccessApiUsageException if given tag already exists, or if given tag is null.
     * @throws DataIntegrityViolationException    if given tag is invalid, or tag with the same name already exists.
     */
    @Override
    public void create(Tag tag) {
        manager.persist(tag);
        manager.flush();
    }

    @Override
    public Tag update(Tag tag) {
        throw new UnsupportedOperationException("Method update() isn't supported in TagDaoImpl");
    }

    /**
     * Deletes tag with given id.
     *
     * @param id id of tag to delete.
     * @throws InvalidDataAccessApiUsageException if given id is null.
     * @throws JpaObjectRetrievalFailureException if tag with given id doesn't exist.
     */
    @Override
    public void delete(Integer id) {
        Optional<Tag> tag = find(id);
        if (tag.isPresent()) {
            manager.remove(tag.get());
        } else {
            throw new EntityNotFoundException(String.format("Tag not found (id=%s)", id));
        }
    }
}
