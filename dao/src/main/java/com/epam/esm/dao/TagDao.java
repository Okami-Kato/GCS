package com.epam.esm.dao;

import com.epam.esm.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TagDao extends JpaRepository<Tag, Integer> {
    String GET_THE_MOST_USED_TAG_OF_USER =
            "WITH tag_certificate_count AS (" +
                    "    SELECT tag_id, COUNT(certificate_id) certificate_count" +
                    "    FROM CERTIFICATE_TAG" +
                    "    WHERE certificate_id in (" +
                    "        SELECT certificate_id" +
                    "        FROM USER_ORDER" +
                    "        WHERE user_id = ?1" +
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

    Page<Tag> findAllByCertificatesId(int certificateId, Pageable pageable);

    @Query(value = GET_THE_MOST_USED_TAG_OF_USER, nativeQuery = true)
    List<Tag> findTheMostUsedTagsOfUser(String userId);

    Optional<Tag> findByName(String name);

    boolean existsByName(String name);
}
