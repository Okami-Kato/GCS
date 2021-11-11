package com.epam.esm.service.impl;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.service.CertificateService;
import com.epam.esm.service.dto.request.CreateCertificateRequest;
import com.epam.esm.service.dto.request.TagRequest;
import com.epam.esm.service.dto.request.UpdateCertificateRequest;
import com.epam.esm.service.dto.response.CertificateItem;
import com.epam.esm.service.dto.response.CertificateResponse;
import com.epam.esm.service.dto.response.TagResponse;
import com.epam.esm.service.exception.EntityNotFoundException;
import com.epam.esm.service.exception.InvalidEntityException;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doReturn;
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

    private final Tag firstTag = Tag.builder()
            .id(1)
            .name("first tag")
            .build();
    private final Tag secondTag = Tag.builder()
            .id(2)
            .name("second tag")
            .build();
    private final Tag thirdTag = Tag.builder()
            .id(3)
            .name("third tag")
            .build();

    private final Certificate certificate = Certificate.builder()
            .id(1)
            .name("name")
            .description("description")
            .price(5)
            .duration(10)
            .tags(Sets.newHashSet(firstTag, secondTag, thirdTag))
            .build();

    @BeforeAll
    void init() {
        certificateDao = mock(CertificateDao.class);
        tagDao = mock(TagDao.class);
        certificateService = new CertificateServiceImpl(certificateDao, tagDao, mapper);
    }

    @Test
    void create() {
        CreateCertificateRequest createRequest =
                new CreateCertificateRequest(certificate.getName(), certificate.getDescription(),
                        certificate.getPrice(), certificate.getDuration(), Sets.newHashSet(
                        new TagRequest(firstTag.getName()),
                        new TagRequest(secondTag.getName()),
                        new TagRequest(thirdTag.getName()))
                );

        CertificateResponse expectedResponse = new CertificateResponse(
                certificate.getId(), certificate.getName(), certificate.getDescription(), certificate.getPrice(),
                certificate.getDuration(), Sets.newHashSet(
                new TagResponse(firstTag.getId(), firstTag.getName()),
                new TagResponse(secondTag.getId(), secondTag.getName()),
                new TagResponse(thirdTag.getId(), thirdTag.getName()))
        );

        when(certificateDao.saveAndFlush(
                argThat(new DeepCertificateMatcher(Certificate.builder()
                        .name(certificate.getName())
                        .description(certificate.getDescription())
                        .price(certificate.getPrice())
                        .duration(certificate.getDuration())
                        .tags(certificate.getTags())
                        .build()
                ))
        )).thenReturn(certificate);


        when(tagDao.findByName(firstTag.getName())).thenReturn(Optional.of(firstTag));
        when(tagDao.findByName(secondTag.getName())).thenReturn(Optional.of(secondTag));
        when(tagDao.findByName(thirdTag.getName())).thenReturn(Optional.empty());
        when(tagDao.save(Tag.builder().name(thirdTag.getName()).build())).thenReturn(thirdTag);

        CertificateResponse actualResponse = certificateService.create(createRequest);
        verify(certificateDao, times(1)).saveAndFlush(any());
        verify(tagDao, times(3)).findByName(anyString());
        verify(tagDao, times(1)).save(any());
        assertEquals(expectedResponse, actualResponse);

        createRequest.setName(null);
        doThrow(DataIntegrityViolationException.class).when(certificateDao).saveAndFlush(
                argThat(new DeepCertificateMatcher(Certificate.builder()
                        .description(certificate.getDescription())
                        .price(certificate.getPrice())
                        .duration(certificate.getDuration())
                        .tags(certificate.getTags())
                        .build()
                ))
        );

        assertThrows(InvalidEntityException.class, () -> certificateService.create(createRequest));
        assertThrows(IllegalArgumentException.class, () -> certificateService.create(null));
    }

    @Test
    void get() {
        int realId = 1;
        int notRealId = 2;

        CertificateResponse expectedResponse = new CertificateResponse(certificate.getId(), certificate.getName(),
                certificate.getDescription(), certificate.getPrice(), certificate.getDuration(),
                Sets.newHashSet(
                        new TagResponse(firstTag.getId(), firstTag.getName()),
                        new TagResponse(secondTag.getId(), secondTag.getName()),
                        new TagResponse(thirdTag.getId(), thirdTag.getName())
                ));

        when(certificateDao.findById(realId)).thenReturn(Optional.of(certificate));
        when(certificateDao.findById(notRealId)).thenReturn(Optional.empty());

        Optional<CertificateResponse> actualResponse = certificateService.findById(realId);
        assertTrue(actualResponse.isPresent());
        assertEquals(expectedResponse, actualResponse.get());

        assertFalse(certificateService.findById(notRealId).isPresent());
    }

    @Test
    void update() {
        Certificate updated = Certificate.builder()
                .id(1)
                .name("new name")
                .description("new description")
                .price(100)
                .duration(50)
                .tags(Sets.newHashSet(firstTag, thirdTag))
                .build();

        UpdateCertificateRequest updateRequest = new UpdateCertificateRequest(
                updated.getId(), updated.getName(), updated.getDescription(), updated.getPrice(), updated.getDuration(),
                Sets.newHashSet(
                        new TagRequest(firstTag.getName()),
                        new TagRequest(thirdTag.getName())));
        CertificateResponse expectedResponse = new CertificateResponse(
                updated.getId(), updated.getName(), updated.getDescription(), updated.getPrice(), updated.getDuration(),
                Sets.newHashSet(
                        new TagResponse(firstTag.getId(), firstTag.getName()),
                        new TagResponse(thirdTag.getId(), thirdTag.getName())));

        when(tagDao.findByName(firstTag.getName())).thenReturn(Optional.of(firstTag));
        when(tagDao.findByName(thirdTag.getName())).thenReturn(Optional.of(thirdTag));
        doReturn(updated).when(certificateDao).saveAndFlush(
                argThat(new DeepCertificateMatcher(mapper.map(updated, Certificate.class)))
        );
        when(certificateDao.existsById(updated.getId())).thenReturn(true);

        CertificateResponse actualResponse = certificateService.update(updateRequest);
        assertEquals(expectedResponse, actualResponse);

        updateRequest.setName(null);
        doThrow(DataIntegrityViolationException.class).when(certificateDao).saveAndFlush(
                argThat(new DeepCertificateMatcher(
                        Certificate.builder()
                                .id(updated.getId())
                                .description(updated.getDescription())
                                .price(updated.getPrice())
                                .duration(updated.getDuration())
                                .tags(updated.getTags())
                                .build()
                )));
        assertThrows(InvalidEntityException.class, () -> certificateService.update(updateRequest));
        assertThrows(IllegalArgumentException.class, () -> certificateService.update(null));
    }

    @Test
    void delete() {
        int realId = 1;
        int notRealId = 2;
        doThrow(EmptyResultDataAccessException.class).when(certificateDao).deleteById(notRealId);
        assertDoesNotThrow(() -> certificateService.delete(realId));
        assertThrows(EntityNotFoundException.class, () -> certificateService.delete(notRealId));
    }

    @Test
    void read() {
        Certificate firstCertificate = Certificate.builder()
                .id(1)
                .name("first")
                .description("first")
                .price(5)
                .duration(10)
                .tags(Sets.newHashSet(firstTag, thirdTag))
                .build();
        Certificate secondCertificate = Certificate.builder()
                .id(2)
                .name("second")
                .description("second")
                .price(5)
                .duration(10)
                .tags(Sets.newHashSet(thirdTag))
                .build();
        Certificate thirdCertificate = Certificate.builder()
                .id(3)
                .name("third")
                .description("third")
                .price(5)
                .duration(10)
                .tags(Sets.newHashSet(firstTag, secondTag, thirdTag))
                .build();

        PageRequest firstPageRequest = PageRequest.of(0, 2);
        PageRequest secondPageRequest = PageRequest.of(1, 2);
        PageRequest thirdPageRequest = PageRequest.of(0, 3);

        when(certificateDao.findAll(firstPageRequest)).thenReturn(
                new PageImpl<>(Arrays.asList(firstCertificate, secondCertificate), firstPageRequest, 3));
        when(certificateDao.findAll(secondPageRequest)).thenReturn(
                new PageImpl<>(Collections.singletonList(thirdCertificate), secondPageRequest, 3));
        when(certificateDao.findAll(thirdPageRequest)).thenReturn(
                new PageImpl<>(Arrays.asList(firstCertificate, secondCertificate, thirdCertificate), thirdPageRequest, 3));

        CertificateItem firstItem = new CertificateItem(
                firstCertificate.getId(), firstCertificate.getName(), firstCertificate.getPrice(),
                Sets.newHashSet(
                        new TagResponse(firstTag.getId(), firstTag.getName()),
                        new TagResponse(thirdTag.getId(), thirdTag.getName())));

        CertificateItem secondItem = new CertificateItem(
                secondCertificate.getId(), secondCertificate.getName(), secondCertificate.getPrice(),
                Sets.newHashSet(new TagResponse(thirdTag.getId(), thirdTag.getName())));

        CertificateItem thirdItem = new CertificateItem(
                thirdCertificate.getId(), thirdCertificate.getName(), thirdCertificate.getPrice(),
                Sets.newHashSet(
                        new TagResponse(firstTag.getId(), firstTag.getName()),
                        new TagResponse(secondTag.getId(), secondTag.getName()),
                        new TagResponse(thirdTag.getId(), thirdTag.getName())));

        assertEquals(Arrays.asList(firstItem, secondItem),
                certificateService.findAll(PageRequest.of(0, 2)).getContent());
        assertEquals(Collections.singletonList(thirdItem),
                certificateService.findAll(PageRequest.of(1, 2)).getContent());
        assertEquals(Arrays.asList(firstItem, secondItem, thirdItem),
                certificateService.findAll(PageRequest.of(0, 3)).getContent());
        assertThrows(IllegalArgumentException.class,
                () -> certificateService.findAll(PageRequest.of(-10, 2)).getContent());
        assertThrows(IllegalArgumentException.class,
                () -> certificateService.findAll(PageRequest.of(0, -10)).getContent());
    }
}