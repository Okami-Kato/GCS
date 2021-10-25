package com.epam.esm.service.impl;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.dao.UserDao;
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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TagServiceImplTest {

    @Autowired
    private ModelMapper mapper;
    private TagDao tagDao;
    private UserDao userDao;

    private CertificateDao certificateDao;

    private TagService tagService;

    private Tag firstTag = new Tag("first");
    private Tag secondTag = new Tag("second");
    private Tag thirdTag = new Tag("third");

    @BeforeAll
    void init() {
        tagDao = mock(TagDao.class);
        certificateDao = mock(CertificateDao.class);
        userDao = mock(UserDao.class);
        tagService = new TagServiceImpl(tagDao, certificateDao, mapper, userDao);
        firstTag.setId(1);
        secondTag.setId(2);
        thirdTag.setId(3);
    }

    @Test
    void create() {
        when(tagDao.existsByName(thirdTag.getName())).thenReturn(true);
        when(tagDao.save(new Tag(firstTag.getName()))).thenReturn(firstTag);
        TagResponse expectedResponse = new TagResponse(firstTag.getId(), firstTag.getName());
        TagResponse actualResponse = tagService.create(new TagRequest(firstTag.getName()));
        assertEquals(expectedResponse, actualResponse);

        when(tagDao.findByName(firstTag.getName())).thenReturn(Optional.of(firstTag));
        assertTrue(tagService.findByName(firstTag.getName()).isPresent());
        assertThrows(EntityExistsException.class, () -> tagService.create(new TagRequest(thirdTag.getName())));
        assertThrows(IllegalArgumentException.class, () -> tagService.create(null));
    }

    @Test
    void delete() {
        int realId = firstTag.getId();
        int notRealId = 2;
        doThrow(EmptyResultDataAccessException.class).when(tagDao).deleteById(notRealId);
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

        when(tagDao.findById(realId)).thenReturn(Optional.of(firstTag));
        when(tagDao.findById(notRealId)).thenReturn(Optional.empty());
        when(tagDao.findByName(realName)).thenReturn(Optional.of(firstTag));
        when(tagDao.findByName(notRealName)).thenReturn(Optional.empty());

        Optional<TagResponse> actualResponse = tagService.findById(realId);
        assertTrue(actualResponse.isPresent());
        assertEquals(expectedResponse, actualResponse.get());

        actualResponse = tagService.findByName(realName);
        assertTrue(actualResponse.isPresent());
        assertEquals(expectedResponse, actualResponse.get());

        assertFalse(tagService.findById(notRealId).isPresent());
        assertFalse(tagService.findByName(notRealName).isPresent());
        assertThrows(IllegalArgumentException.class, () -> tagService.findByName(null));
    }

    @Test
    void read() {
        int certificateId = 1;
        when(certificateDao.findById(certificateId)).thenReturn(Optional.of(
                new Certificate("name", "description", 10, 12)));

        PageRequest firstPageRequest = PageRequest.of(0, 2);
        PageRequest secondPageRequest = PageRequest.of(1, 2);
        PageRequest thirdPageRequest = PageRequest.of(0, 3);

        when(tagDao.findAll(firstPageRequest)).thenReturn(
                new PageImpl<>(Arrays.asList(firstTag, secondTag), firstPageRequest, 3));
        when(tagDao.findAll(secondPageRequest)).thenReturn(
                new PageImpl<>(Collections.singletonList(thirdTag), secondPageRequest, 3));
        when(tagDao.findAll(thirdPageRequest)).thenReturn(
                new PageImpl<>(Arrays.asList(firstTag, secondTag, thirdTag), thirdPageRequest, 3));
        when(tagDao.findAllByCertificatesId(certificateId, firstPageRequest)).thenReturn(
                new PageImpl<>(Arrays.asList(firstTag, thirdTag), firstPageRequest, 2));

        TagResponse firstResponse = new TagResponse(firstTag.getId(), firstTag.getName());
        TagResponse secondResponse = new TagResponse(secondTag.getId(), secondTag.getName());
        TagResponse thirdResponse = new TagResponse(thirdTag.getId(), thirdTag.getName());

        assertEquals(Arrays.asList(firstResponse, secondResponse), tagService.findAll(firstPageRequest).getContent());
        assertEquals(Collections.singletonList(thirdResponse), tagService.findAll(secondPageRequest).getContent());
        assertEquals(Arrays.asList(firstResponse, secondResponse, thirdResponse), tagService.findAll(thirdPageRequest).getContent());

    }
}