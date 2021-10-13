package com.epam.esm.service.impl;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.service.CertificateService;
import com.epam.esm.service.dto.request.CreateCertificateRequest;
import com.epam.esm.service.dto.request.CreateTagRequest;
import com.epam.esm.service.dto.request.UpdateCertificateRequest;
import com.epam.esm.service.dto.response.CertificateItem;
import com.epam.esm.service.dto.response.CertificateResponse;
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
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.intThat;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CertificateServiceImplTest {

    @Autowired
    private ModelMapper mapper;
    private CertificateDao certificateDao;
    private TagDao tagDao;

    private CertificateService certificateService;

    private Tag firstTag = new Tag("first");
    private Tag secondTag = new Tag("second");
    private Tag thirdTag = new Tag("third");

    Certificate certificate = new Certificate("name", "description", 5, 10,
            new HashSet<>(Arrays.asList(firstTag, secondTag, thirdTag)));

    @BeforeAll
    void init() {
        certificateDao = mock(CertificateDao.class);
        tagDao = mock(TagDao.class);
        certificateService = new CertificateServiceImpl(certificateDao, tagDao, mapper);
        certificate.setId(1);
        firstTag.setId(1);
        secondTag.setId(2);
        thirdTag.setId(3);
    }

    @Test
    void create() {
        CreateCertificateRequest createRequest =
                new CreateCertificateRequest(certificate.getName(), certificate.getDescription(),
                        certificate.getPrice(), certificate.getDuration(),
                        new HashSet<>(Arrays.asList(
                                new CreateTagRequest(firstTag.getName()),
                                new CreateTagRequest(secondTag.getName()),
                                new CreateTagRequest(thirdTag.getName()))));

        CertificateResponse expectedResponse = new CertificateResponse();
        expectedResponse.setId(certificate.getId());
        expectedResponse.setName(certificate.getName());
        expectedResponse.setDescription(certificate.getDescription());
        expectedResponse.setPrice(certificate.getPrice());
        expectedResponse.setDuration(certificate.getDuration());
        expectedResponse.setTags(new HashSet<>(
                Arrays.asList(
                        new TagResponse(firstTag.getId(), firstTag.getName()),
                        new TagResponse(secondTag.getId(), secondTag.getName()),
                        new TagResponse(thirdTag.getId(), thirdTag.getName()))));

        doThrow(InvalidDataAccessApiUsageException.class).when(certificateDao).create(isNull());

        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            ((Certificate) args[0]).setId(certificate.getId());
            return null;
        }).when(certificateDao).create(new Certificate("name", "description", 5, 10,
                new HashSet<>(Arrays.asList(firstTag, secondTag, thirdTag))));

        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            ((Tag) args[0]).setId(thirdTag.getId());
            return null;
        }).when(tagDao).create(new Tag("third"));

        when(tagDao.get("first")).thenReturn(Optional.of(firstTag));
        when(tagDao.get("second")).thenReturn(Optional.of(secondTag));
        when(tagDao.get("third")).thenReturn(Optional.empty());

        CertificateResponse actualResponse = certificateService.create(createRequest);
        verify(certificateDao, times(1)).create(any());
        verify(tagDao, times(3)).get(anyString());
        verify(tagDao, times(1)).create(any());
        assertEquals(expectedResponse, actualResponse);

        createRequest.setName(null);
        doThrow(DataIntegrityViolationException.class).when(certificateDao).create(mapper.map(createRequest, Certificate.class));

        assertThrows(ServiceException.class, () -> certificateService.create(createRequest));
        assertThrows(IllegalArgumentException.class, () -> certificateService.create(null));
    }

    @Test
    void get() {
        int realId = 1;
        int notRealId = 2;

        CertificateResponse expectedResponse = new CertificateResponse();
        expectedResponse.setId(certificate.getId());
        expectedResponse.setName(certificate.getName());
        expectedResponse.setDescription(certificate.getDescription());
        expectedResponse.setPrice(certificate.getPrice());
        expectedResponse.setDuration(certificate.getDuration());
        expectedResponse.setTags(new HashSet<>(
                Arrays.asList(
                        new TagResponse(firstTag.getId(), firstTag.getName()),
                        new TagResponse(secondTag.getId(), secondTag.getName()),
                        new TagResponse(thirdTag.getId(), thirdTag.getName()))));

        when(certificateDao.get(realId)).thenReturn(Optional.of(certificate));
        when(certificateDao.get(notRealId)).thenReturn(Optional.empty());
        when(certificateDao.get(null)).thenThrow(InvalidDataAccessApiUsageException.class);

        Optional<CertificateResponse> actualResponse = certificateService.get(realId);
        assertTrue(actualResponse.isPresent());
        assertEquals(expectedResponse, actualResponse.get());

        assertFalse(certificateService.get(notRealId).isPresent());
    }

    @Test
    void update() {
        Certificate updated = new Certificate("new name", "new description", 100, 50,
                new HashSet<>(Arrays.asList(firstTag, thirdTag)));
        updated.setId(1);

        UpdateCertificateRequest updateRequest = new UpdateCertificateRequest();
        updateRequest.setId(updated.getId());
        updateRequest.setName(updated.getName());
        updateRequest.setDescription(updated.getDescription());
        updateRequest.setPrice(updated.getPrice());
        updateRequest.setDuration(updated.getDuration());
        updateRequest.setTags(new HashSet<>(Arrays.asList(
                new CreateTagRequest(firstTag.getName()),
                new CreateTagRequest(thirdTag.getName()))));

        CertificateResponse expectedResponse = new CertificateResponse();
        expectedResponse.setId(updated.getId());
        expectedResponse.setName(updated.getName());
        expectedResponse.setDescription(updated.getDescription());
        expectedResponse.setPrice(updated.getPrice());
        expectedResponse.setDuration(updated.getDuration());
        expectedResponse.setTags(new HashSet<>(
                Arrays.asList(
                        new TagResponse(firstTag.getId(), firstTag.getName()),
                        new TagResponse(thirdTag.getId(), thirdTag.getName()))));

        when(tagDao.get(firstTag.getName())).thenReturn(Optional.of(firstTag));
        when(tagDao.get(thirdTag.getName())).thenReturn(Optional.of(thirdTag));
        when(certificateDao.update(updated)).thenReturn(updated);

        CertificateResponse actualResponse = certificateService.update(updateRequest);
        assertEquals(expectedResponse, actualResponse);

        updateRequest.setName(null);
        doThrow(DataIntegrityViolationException.class).when(certificateDao).update(mapper.map(updateRequest, Certificate.class));
        assertThrows(ServiceException.class, () -> certificateService.update(updateRequest));
        assertThrows(IllegalArgumentException.class, () -> certificateService.update(null));
    }

    @Test
    void delete() {
        int realId = 1;
        int notRealId = 2;
        doThrow(InvalidDataAccessApiUsageException.class).when(certificateDao).delete(notRealId);
        doThrow(InvalidDataAccessApiUsageException.class).when(certificateDao).delete(isNull());
        assertDoesNotThrow(() -> certificateService.delete(realId));
        assertThrows(ServiceException.class, () -> certificateService.delete(notRealId));
    }

    @Test
    void read() {
        Certificate firstCertificate = new Certificate("first", "first", 5, 10,
                new HashSet<>(Arrays.asList(firstTag, thirdTag)));
        Certificate secondCertificate = new Certificate("second", "second", 5, 10,
                new HashSet<>(Collections.singletonList(thirdTag)));
        Certificate thirdCertificate = new Certificate("third", "third", 5, 10,
                new HashSet<>(Arrays.asList(firstTag, secondTag, thirdTag)));
        firstCertificate.setId(1);
        secondCertificate.setId(2);
        thirdCertificate.setId(3);
        when(certificateDao.getAll(1, 2)).thenReturn(Arrays.asList(firstCertificate, secondCertificate));
        when(certificateDao.getAll(2, 2)).thenReturn(Collections.singletonList(thirdCertificate));
        when(certificateDao.getAll(1, 3)).thenReturn(Arrays.asList(firstCertificate, secondCertificate, thirdCertificate));
        when(certificateDao.getAll(intThat(i -> i < 0), anyInt())).thenThrow(InvalidDataAccessApiUsageException.class);
        when(certificateDao.getAll(anyInt(), intThat(i -> i < 0))).thenThrow(InvalidDataAccessApiUsageException.class);
        CertificateItem firstItem = new CertificateItem(
                firstCertificate.getId(), firstCertificate.getName(), firstCertificate.getPrice(),
                new HashSet<>(
                        Arrays.asList(
                                new TagResponse(firstTag.getId(), firstTag.getName()),
                                new TagResponse(thirdTag.getId(), thirdTag.getName()))));

        CertificateItem secondItem = new CertificateItem(
                secondCertificate.getId(), secondCertificate.getName(), secondCertificate.getPrice(),
                new HashSet<>(Collections.singletonList(new TagResponse(thirdTag.getId(), thirdTag.getName()))));

        CertificateItem thirdItem = new CertificateItem(
                thirdCertificate.getId(), thirdCertificate.getName(), thirdCertificate.getPrice(),
                new HashSet<>(
                        Arrays.asList(
                                new TagResponse(firstTag.getId(), firstTag.getName()),
                                new TagResponse(secondTag.getId(), secondTag.getName()),
                                new TagResponse(thirdTag.getId(), thirdTag.getName()))));

        assertEquals(Arrays.asList(firstItem, secondItem), certificateService.getAll(1, 2));
        assertEquals(Collections.singletonList(thirdItem), certificateService.getAll(2, 2));
        assertEquals(Arrays.asList(firstItem, secondItem, thirdItem), certificateService.getAll(1, 3));
        assertThrows(ServiceException.class, () -> certificateService.getAll(-1, 2));
        assertThrows(ServiceException.class, () -> certificateService.getAll(1, -1));
    }
}