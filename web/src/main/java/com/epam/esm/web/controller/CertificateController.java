package com.epam.esm.web.controller;

import com.epam.esm.service.CertificateService;
import com.epam.esm.service.dto.request.CreateCertificateRequest;
import com.epam.esm.service.dto.request.UpdateCertificateRequest;
import com.epam.esm.service.dto.response.CertificateItem;
import com.epam.esm.service.dto.response.CertificateResponse;
import com.epam.esm.service.dto.response.TagResponse;
import com.epam.esm.service.exception.ServiceException;
import com.epam.esm.util.CertificateFilter;
import com.epam.esm.util.CertificateFilter.CertificateFilterBuilder;
import com.epam.esm.util.Sort;
import com.epam.esm.util.Sort.Order;
import com.epam.esm.util.SortDirection;
import com.epam.esm.web.exception.BadRequestException;
import com.epam.esm.web.exception.EntityNotFoundException;
import com.epam.esm.web.exception.ErrorCode;
import com.epam.esm.web.processor.CertificatePostProcessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Validated
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class CertificateController {
    private final CertificateService certificateService;

    private final CertificatePostProcessor certificatePostProcessor;

    private final ObjectMapper objectMapper;

    @Autowired
    public CertificateController(CertificatePostProcessor certificatePostProcessor, CertificateService certificateService, ObjectMapper objectMapper) {
        this.certificatePostProcessor = certificatePostProcessor;
        this.certificateService = certificateService;
        this.objectMapper = objectMapper;
    }

    /**
     * Retrieves all gift certificates, that match given properties.
     *
     * @param page            number of page.
     * @param size            size of page.
     * @param namePart        part in name.
     * @param descriptionPart part in description.
     * @param tagIds          array of tag ids.
     * @param sort            sorting to be applied.
     * @return list of found certificates.
     * @throws BadRequestException if given parameters are invalid.
     */
    @GetMapping(value = "/certificates")
    public CollectionModel<? extends CertificateItem> getAllCertificates(@RequestParam(defaultValue = "1")
                                                               @Positive(message = "Page number must be a positive number") Integer page,
                                                               @RequestParam(defaultValue = "5")
                                                               @Positive(message = "Size must be a positive number") Integer size,
                                                               @RequestParam Optional<String> namePart,
                                                               @RequestParam Optional<String> descriptionPart,
                                                               @RequestParam Optional<Integer[]> tagIds,
                                                               @RequestParam Optional<List<
                                                                       @NotBlank
                                                                       @Pattern(regexp = "^[\\w]+[.](asc|desc)$",
                                                                               message = "Sort property must be of pattern " +
                                                                                       "property.sortDirection (name.asc, price.desc)")
                                                                               String>> sort) {
        try {
            CertificateFilterBuilder filterBuilder = CertificateFilter.newBuilder();
            namePart.ifPresent(filterBuilder::withPartInName);
            descriptionPart.ifPresent(filterBuilder::withPartInDescription);
            tagIds.ifPresent(filterBuilder::withTags);
            if (sort.isPresent()) {
                List<Order> orders = new LinkedList<>();
                for (String s : sort.get()) {
                    String[] split = s.split("\\.");
                    orders.add(new Order(split[0], SortDirection.valueOf(split[1].toUpperCase())));
                }
                filterBuilder.withSort(Sort.by(orders));
            }
            List<CertificateItem> certificateList = certificateService.getAll(page, size, filterBuilder.build());
            CollectionModel<? extends CertificateItem> response = certificatePostProcessor.processCollection(certificateList);
            return response.add(linkTo(methodOn(CertificateController.class)
                    .getAllCertificates(page, size, namePart, descriptionPart, tagIds, sort))
                    .withSelfRel());
        } catch (ServiceException e) {
            throw new BadRequestException(ErrorCode.CERTIFICATE_BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Retrieves certificate with given id.
     *
     * @param id id of desired certificate.
     * @return found certificate.
     * @throws EntityNotFoundException if certificate wasn't found.
     */
    @GetMapping(value = "/certificates/{id}")
    public CertificateResponse getCertificate(@PathVariable int id) {
        Optional<CertificateResponse> response = certificateService.get(id);
        response.ifPresent(certificatePostProcessor::processEntity);
        return response.orElseThrow(() -> new EntityNotFoundException(ErrorCode.CERTIFICATE_NOT_FOUND, "id=" + id));
    }

    /**
     * Creates new certificate from given {@link CreateCertificateRequest}.
     *
     * @param certificate certificate to be created.
     * @return created certificate, if certificate is valid and service call was successful.
     * @throws BadRequestException if given certificate was invalid.
     */
    @PostMapping(value = "/certificates")
    @ResponseStatus(HttpStatus.CREATED)
    public CertificateResponse createCertificate(@Valid @RequestBody CreateCertificateRequest certificate) {
        try {
            CertificateResponse response = certificateService.create(certificate);
            certificatePostProcessor.processEntity(response);
            return response;
        } catch (ServiceException e) {
            throw new BadRequestException(ErrorCode.CERTIFICATE_BAD_REQUEST, e.getMessage());
        }
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
        try {
            certificateService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (ServiceException e) {
            throw new EntityNotFoundException(ErrorCode.CERTIFICATE_NOT_FOUND, "id=" + id);
        }
    }

    /**
     * Updates certificate with given id. New values are taken from not null fields of given certificate.
     *
     * @param id    id of certificate to be updated
     * @param patch array of patch methods.
     * @return updated certificate.
     * @throws EntityNotFoundException if certificate wasn't found.
     * @throws BadRequestException     if updated certificate isn't valid, or if failed to apply patch.
     */
    @PatchMapping(path = "/certificates/{id}", consumes = "application/json-patch+json")
    public CertificateResponse updateCertificate(@PathVariable int id, @RequestBody JsonPatch patch) {
        CertificateResponse certificate = certificateService.get(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.CERTIFICATE_NOT_FOUND, "id=" + id));

        UpdateCertificateRequest updateRequest = new UpdateCertificateRequest(certificate.getId(), certificate.getName(),
                certificate.getDescription(), certificate.getPrice(), certificate.getDuration(),
                certificate.getTags().stream().map(TagResponse::getName).collect(Collectors.toSet()));
        try {
            UpdateCertificateRequest certificatePatched = applyPatchToCertificate(patch, updateRequest);
            CertificateResponse response = updateCertificate(certificatePatched);
            certificatePostProcessor.processEntity(response);
            return response;
        } catch (ServiceException | JsonPatchException | JsonProcessingException e) {
            throw new BadRequestException(ErrorCode.CERTIFICATE_BAD_REQUEST, e.getMessage());
        }
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
