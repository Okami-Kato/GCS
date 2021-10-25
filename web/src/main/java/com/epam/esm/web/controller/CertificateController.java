package com.epam.esm.web.controller;

import com.epam.esm.entity.Certificate;
import com.epam.esm.service.CertificateService;
import com.epam.esm.service.dto.request.CreateCertificateRequest;
import com.epam.esm.service.dto.request.TagRequest;
import com.epam.esm.service.dto.request.UpdateCertificateRequest;
import com.epam.esm.service.dto.response.CertificateItem;
import com.epam.esm.service.dto.response.CertificateResponse;
import com.epam.esm.service.exception.EntityNotFoundException;
import com.epam.esm.service.exception.ErrorCode;
import com.epam.esm.service.exception.InvalidEntityException;
import com.epam.esm.web.linker.CertificateLinker;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.epam.esm.service.specification.CertificateSpecifications.descriptionLike;
import static com.epam.esm.service.specification.CertificateSpecifications.nameLike;
import static com.epam.esm.service.specification.CertificateSpecifications.withTags;

@RestController
@Validated
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class CertificateController {
    private final CertificateService certificateService;
    private final CertificateLinker certificateLinker;
    private final ObjectMapper objectMapper;
    private final PagedResourcesAssembler pagedResourcesAssembler;

    @Autowired
    public CertificateController(CertificateLinker certificateLinker, CertificateService certificateService,
                                 ObjectMapper objectMapper, PagedResourcesAssembler pagedResourcesAssembler) {
        this.certificateLinker = certificateLinker;
        this.certificateService = certificateService;
        this.objectMapper = objectMapper;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    /**
     * Retrieves all gift certificates, that match given properties.
     *
     * @param pageable        pagination restrictions.
     * @param namePart        part in name.
     * @param descriptionPart part in description.
     * @param tagNames        array of tag names.
     * @return page of found certificates.
     */
    @GetMapping(value = "/certificates")
    public PagedModel<CertificateItem> findAllCertificates(Pageable pageable,
                                                           @RequestParam Optional<String> namePart,
                                                           @RequestParam Optional<String> descriptionPart,
                                                           @RequestParam Optional<String[]> tagNames) {
        Specification<Certificate> specification = Specification.where(null);
        if (namePart.isPresent()) {
            specification = specification.and(nameLike(namePart.get()));
        }
        if (descriptionPart.isPresent()) {
            specification = specification.and(descriptionLike(descriptionPart.get()));
        }
        if (tagNames.isPresent()) {
            specification = specification.and(withTags(tagNames.get()));
        }
        Page<CertificateItem> certificatePage = certificateService.findAll(specification, pageable);
        certificateLinker.processCollection(certificatePage.getContent());
        return pagedResourcesAssembler.toModel(certificatePage);
    }

    /**
     * Retrieves certificate with given id.
     *
     * @param id id of desired certificate.
     * @return found certificate.
     * @throws EntityNotFoundException if certificate wasn't found.
     */
    @GetMapping(value = "/certificates/{id}")
    public CertificateResponse findCertificate(@PathVariable int id) {
        Optional<CertificateResponse> response = certificateService.findById(id);
        response.ifPresent(certificateLinker::processEntity);
        return response.orElseThrow(() -> new EntityNotFoundException("id=" + id, ErrorCode.CERTIFICATE_NOT_FOUND));
    }

    /**
     * Creates new certificate from given {@link CreateCertificateRequest}.
     *
     * @param certificate certificate to be created.
     * @return created certificate, if certificate is valid and service call was successful.
     * @throws IllegalArgumentException if certificate is null.
     * @throws InvalidEntityException   if certificate is invalid.
     */
    @PostMapping(value = "/certificates")
    @ResponseStatus(HttpStatus.CREATED)
    public CertificateResponse createCertificate(@Valid @RequestBody CreateCertificateRequest certificate) {
        CertificateResponse response = certificateService.create(certificate);
        certificateLinker.processEntity(response);
        return response;
    }

    /**
     * Deletes certificate with given id.
     *
     * @param id id of desired certificate.
     * @throws EntityNotFoundException if certificate wasn't found.
     */
    @DeleteMapping(value = "/certificates/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteCertificate(@PathVariable int id) {
        certificateService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Updates certificate with given id.
     *
     * @param id    id of certificate to be updated
     * @param patch array of patch methods.
     * @return updated certificate.
     * @throws EntityNotFoundException if certificate wasn't found.
     * @throws InvalidEntityException  if updated certificate isn't valid.
     * @throws JsonPatchException      if failed to apply patch.
     * @throws JsonProcessingException if structural conversion fails.
     */
    @PatchMapping(path = "/certificates/{id}", consumes = "application/json-patch+json")
    public CertificateResponse updateCertificate(@PathVariable int id, @RequestBody JsonPatch patch) throws JsonPatchException, JsonProcessingException {
        CertificateResponse certificate = certificateService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("id=" + id, ErrorCode.CERTIFICATE_NOT_FOUND));

        UpdateCertificateRequest updateRequest = new UpdateCertificateRequest(certificate.getId(), certificate.getName(),
                certificate.getDescription(), certificate.getPrice(), certificate.getDuration(),
                certificate.getTags().stream().map(tag -> new TagRequest(tag.getName())).collect(Collectors.toSet()));
        UpdateCertificateRequest certificatePatched = applyPatchToCertificate(patch, updateRequest);
        CertificateResponse response = updateCertificate(certificatePatched);
        certificateLinker.processEntity(response);
        return response;
    }

    private CertificateResponse updateCertificate(UpdateCertificateRequest updateRequest) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<UpdateCertificateRequest>> constraintViolations = validator.validate(updateRequest);
        if (!constraintViolations.isEmpty()) {
            throw new ConstraintViolationException(constraintViolations);
        }
        return certificateService.update(updateRequest);
    }

    private UpdateCertificateRequest applyPatchToCertificate(JsonPatch patch, UpdateCertificateRequest certificate) throws JsonPatchException, JsonProcessingException {
        JsonNode patched = patch.apply(objectMapper.valueToTree(certificate));
        return objectMapper.treeToValue(patched, UpdateCertificateRequest.class);
    }
}
