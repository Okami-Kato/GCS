package com.epam.esm.service.impl;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.service.TagService;
import com.epam.esm.service.dto.request.TagRequest;
import com.epam.esm.service.dto.response.TagResponse;
import com.epam.esm.service.exception.EntityExistsException;
import com.epam.esm.service.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.intThat;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TagServiceImplTest {

    @Autowired
    private ModelMapper mapper;
    private TagDao tagDao;
    private CertificateDao certificateDao;

    private TagService tagService;

    private Tag firstTag = new Tag("first");
    private Tag secondTag = new Tag("second");
    private Tag thirdTag = new Tag("third");

    @BeforeAll
    void init() {
        tagDao = mock(TagDao.class);
        certificateDao = mock(CertificateDao.class);
        tagService = new TagServiceImpl(tagDao, certificateDao, mapper);
        firstTag.setId(1);
        secondTag.setId(2);
        thirdTag.setId(3);
    }

    @Test
    void create() {
        when(tagDao.get(thirdTag.getName())).thenReturn(Optional.of(thirdTag));
        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            ((Tag) args[0]).setId(firstTag.getId());
            return null;
        }).when(tagDao).create(new Tag(firstTag.getName()));
        TagResponse expectedResponse = new TagResponse(firstTag.getId(), firstTag.getName());
        TagResponse actualResponse = tagService.create(new TagRequest(firstTag.getName()));
        assertEquals(expectedResponse, actualResponse);

        when(tagDao.get(firstTag.getName())).thenReturn(Optional.of(firstTag));
        assertTrue(tagService.get(firstTag.getName()).isPresent());
        assertThrows(EntityExistsException.class, () -> tagService.create(new TagRequest(thirdTag.getName())));
        assertThrows(IllegalArgumentException.class, () -> tagService.create(null));
    }

    @Test
    void delete() {
        int realId = firstTag.getId();
        int notRealId = 2;
        doThrow(JpaObjectRetrievalFailureException.class).when(tagDao).delete(notRealId);
        assertDoesNotThrow(() -> tagService.delete(realId));
        assertThrows(EntityNotFoundException.class, () -> tagService.delete(notRealId));
    }

    @Test
    void get() {
        int realId = firstTag.getId();
        int notRealId = 2;
        String realName = firstTag.getName();
        String notRealName = "not real name";

        TagResponse expectedResponse = new TagResponse(realId, firstTag.getName());

        when(tagDao.get(realId)).thenReturn(Optional.of(firstTag));
        when(tagDao.get(notRealId)).thenReturn(Optional.empty());
        when(tagDao.get(realName)).thenReturn(Optional.of(firstTag));
        when(tagDao.get(notRealName)).thenReturn(Optional.empty());
        when(tagDao.get((String) isNull())).thenThrow(InvalidDataAccessApiUsageException.class);

        Optional<TagResponse> actualResponse = tagService.get(realId);
        assertTrue(actualResponse.isPresent());
        assertEquals(expectedResponse, actualResponse.get());

        actualResponse = tagService.get(realName);
        assertTrue(actualResponse.isPresent());
        assertEquals(expectedResponse, actualResponse.get());

        assertFalse(tagService.get(notRealId).isPresent());
        assertFalse(tagService.get(notRealName).isPresent());
        assertThrows(IllegalArgumentException.class, () -> tagService.get(null));
    }

    @Test
    void read() {
        int certificateId = 1;
        when(certificateDao.get(certificateId)).thenReturn(Optional.of(
                new Certificate("name", "description", 10, 12)));
        when(tagDao.getAll(1, 2)).thenReturn(Arrays.asList(firstTag, secondTag));
        when(tagDao.getAll(2, 2)).thenReturn(Collections.singletonList(thirdTag));
        when(tagDao.getAll(1, 3)).thenReturn(Arrays.asList(firstTag, secondTag, thirdTag));
        when(tagDao.findAllByCertificateId(1, 2, certificateId)).thenReturn(Arrays.asList(firstTag, thirdTag));
        when(tagDao.getAll(intThat(i -> i < 0), anyInt())).thenThrow(InvalidDataAccessApiUsageException.class);
        when(tagDao.getAll(anyInt(), intThat(i -> i < 0))).thenThrow(InvalidDataAccessApiUsageException.class);
        when(tagDao.findAllByCertificateId(intThat(i -> i < 0), anyInt(), anyInt())).thenThrow(InvalidDataAccessApiUsageException.class);
        when(tagDao.findAllByCertificateId(anyInt(), intThat(i -> i < 0), anyInt())).thenThrow(InvalidDataAccessApiUsageException.class);

        TagResponse firstResponse = new TagResponse(firstTag.getId(), firstTag.getName());
        TagResponse secondResponse = new TagResponse(secondTag.getId(), secondTag.getName());
        TagResponse thirdResponse = new TagResponse(thirdTag.getId(), thirdTag.getName());

        assertEquals(Arrays.asList(firstResponse, secondResponse), tagService.getAll(1, 2));
        assertEquals(Collections.singletonList(thirdResponse), tagService.getAll(2, 2));
        assertEquals(Arrays.asList(firstResponse, secondResponse, thirdResponse), tagService.getAll(1, 3));

        assertEquals(Arrays.asList(firstResponse, thirdResponse), tagService.findAllByCertificateId(1, 2, certificateId));

        assertThrows(IllegalArgumentException.class, () -> tagService.getAll(-1, 2));
        assertThrows(IllegalArgumentException.class, () -> tagService.getAll(1, -1));
        assertThrows(IllegalArgumentException.class, () -> tagService.findAllByCertificateId(-1, 2, certificateId));
        assertThrows(IllegalArgumentException.class, () -> tagService.findAllByCertificateId(1, -1, certificateId));
    }
}