package com.epam.esm.service.impl;

import com.epam.esm.dao.TagDao;
import com.epam.esm.entity.Tag;
import com.epam.esm.service.TagService;
import com.epam.esm.service.dto.request.CreateTagRequest;
import com.epam.esm.service.dto.response.TagResponse;
import com.epam.esm.service.exception.ServiceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;

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

    private TagService tagService;

    private Tag firstTag = new Tag("first");
    private Tag secondTag = new Tag("second");
    private Tag thirdTag = new Tag("third");

    @BeforeAll
    void init() {
        tagDao = mock(TagDao.class);
        tagService = new TagServiceImpl(tagDao, mapper);
        firstTag.setId(1);
        secondTag.setId(2);
        thirdTag.setId(3);
    }

    @Test
    void create() {
        doThrow(DataIntegrityViolationException.class).when(tagDao).create(new Tag(thirdTag.getName()));
        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            ((Tag) args[0]).setId(firstTag.getId());
            return null;
        }).when(tagDao).create(new Tag(firstTag.getName()));
        TagResponse actualResponse = tagService.create(new CreateTagRequest(firstTag.getName()));
        TagResponse expectedResponse = new TagResponse(firstTag.getId(), firstTag.getName());
        assertEquals(expectedResponse, actualResponse);
        assertThrows(ServiceException.class, () -> tagService.create(new CreateTagRequest(thirdTag.getName())));
        assertThrows(IllegalArgumentException.class, () -> tagService.create(null));
    }

    @Test
    void delete() {
        int realId = firstTag.getId();
        int notRealId = 2;
        doThrow(InvalidDataAccessApiUsageException.class).when(tagDao).delete(notRealId);
        assertDoesNotThrow(() -> tagService.delete(realId));
        assertThrows(ServiceException.class, () -> tagService.delete(notRealId));
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
        assertThrows(ServiceException.class, () -> tagService.get(null));
    }

    @Test
    void getAll() {
        int certificateId = 1;
        when(tagDao.getAll(1, 2)).thenReturn(Arrays.asList(firstTag, secondTag));
        when(tagDao.getAll(2, 2)).thenReturn(Collections.singletonList(thirdTag));
        when(tagDao.getAll(1, 3)).thenReturn(Arrays.asList(firstTag, secondTag, thirdTag));
        when(tagDao.getAll(1, 2, certificateId)).thenReturn(Arrays.asList(firstTag, thirdTag));
        when(tagDao.getAll(intThat(i -> i < 0), anyInt())).thenThrow(InvalidDataAccessApiUsageException.class);
        when(tagDao.getAll(anyInt(), intThat(i -> i < 0))).thenThrow(InvalidDataAccessApiUsageException.class);
        when(tagDao.getAll(intThat(i -> i < 0), anyInt(), anyInt())).thenThrow(InvalidDataAccessApiUsageException.class);
        when(tagDao.getAll(anyInt(), intThat(i -> i < 0), anyInt())).thenThrow(InvalidDataAccessApiUsageException.class);

        TagResponse firstResponse = new TagResponse(firstTag.getId(), firstTag.getName());
        TagResponse secondResponse = new TagResponse(secondTag.getId(), secondTag.getName());
        TagResponse thirdResponse = new TagResponse(thirdTag.getId(), thirdTag.getName());

        assertEquals(Arrays.asList(firstResponse, secondResponse), tagService.getAll(1, 2));
        assertEquals(Collections.singletonList(thirdResponse), tagService.getAll(2, 2));
        assertEquals(Arrays.asList(firstResponse, secondResponse, thirdResponse), tagService.getAll(1, 3));

        assertEquals(Arrays.asList(firstResponse, thirdResponse), tagService.getAll(1, 2, certificateId));

        assertThrows(ServiceException.class, () -> tagService.getAll(-1, 2));
        assertThrows(ServiceException.class, () -> tagService.getAll(1, -1));
        assertThrows(ServiceException.class, () -> tagService.getAll(-1, 2, certificateId));
        assertThrows(ServiceException.class, () -> tagService.getAll(1, -1, certificateId));
    }
}