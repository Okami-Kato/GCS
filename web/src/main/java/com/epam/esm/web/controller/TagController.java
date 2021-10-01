package com.epam.esm.web.controller;

import com.epam.esm.service.CertificateService;
import com.epam.esm.service.TagService;
import com.epam.esm.service.dto.request.CreateTagRequest;
import com.epam.esm.service.dto.response.CertificateItem;
import com.epam.esm.service.dto.response.TagResponse;
import com.epam.esm.service.exception.ServiceException;
import com.epam.esm.util.CertificateFilter;
import com.epam.esm.web.exception.BadRequestException;
import com.epam.esm.web.exception.EntityNotFoundException;
import com.epam.esm.web.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Optional;

@RestController
@Validated
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class TagController {
    private final CertificateService certificateService;
    private final TagService tagService;
    private final ObjectMapper objectMapper;
    private final SmartValidator validator;

    @Autowired
    public TagController(CertificateService certificateService, TagService tagService, ObjectMapper objectMapper, SmartValidator validator) {
        this.certificateService = certificateService;
        this.tagService = tagService;
        this.objectMapper = objectMapper;
        this.validator = validator;
    }

    /**
     * Retrieves all tags.
     *
     * @param page number of page.
     * @param size size of page.
     * @return list of found tags.
     * @throws BadRequestException if given parameters are invalid.
     */
    @GetMapping(value = "/tags")
    public List<TagResponse> getAllTags(@RequestParam(defaultValue = "1")
                                        @Positive(message = "Page number must be a positive number") int page,
                                        @RequestParam(defaultValue = "5")
                                        @Positive(message = "Size must be a positive number") int size) {
        try {
            return tagService.getAll(page, size);
        } catch (ServiceException e) {
            throw new BadRequestException(ErrorCode.TAG_BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Retrieves all certificates assigned to given tag.
     *
     * @param page number of page.
     * @param size size of page.
     * @param id   id of tag.
     * @return list of found certificates.
     * @throws BadRequestException if given parameters are invalid.
     */
    @GetMapping(value = "/tags/{id}/certificates")
    public List<CertificateItem> getCertificates(@RequestParam(defaultValue = "1")
                                                 @Positive(message = "Page number must be a positive number") int page,
                                                 @RequestParam(defaultValue = "5")
                                                 @Positive(message = "Size must be a positive number") int size,
                                                 @PathVariable int id) {
        try {
            return certificateService.getAll(page, size, CertificateFilter.newBuilder().withTags(id).build());
        } catch (ServiceException e) {
            throw new BadRequestException(ErrorCode.CERTIFICATE_BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Retrieves the most widely used tag of a user with the highest cost of all orders.
     *
     * @return found tag.
     * @throws EntityNotFoundException if tag wasn't found.
     */
    @GetMapping(value = "/tags/theMostUsedTagOfUserWithTheHighestCost")
    public TagResponse getTheMostUsedTagOfUserWithTheHighestCost() {
        Optional<TagResponse> tag = tagService.getTheMostUsedTagOfUserWithTheHighestCost();
        return tag.orElseThrow(() -> new EntityNotFoundException(ErrorCode.CERTIFICATE_NOT_FOUND,
                "The most widely used tag of a user with the highest cost of all orders"));
    }

    /**
     * Retrieves tag with given id.
     *
     * @param id id of desired tag.
     * @return found tag.
     * @throws EntityNotFoundException if tag wasn't found.
     */
    @GetMapping(value = "/tags/{id}")
    public TagResponse getTag(@PathVariable int id) {
        Optional<TagResponse> tag = tagService.get(id);
        return tag.orElseThrow(() -> new EntityNotFoundException(ErrorCode.CERTIFICATE_NOT_FOUND, "id=" + id));
    }

    /**
     * Creates new tag from given {@link CreateTagRequest}.
     *
     * @param tag tag to be created.
     * @return created tag, if tag is valid and service call was successful.
     * @throws BadRequestException if given tag was invalid.
     */
    @PostMapping(value = "/tags")
    @ResponseStatus(HttpStatus.CREATED)
    public TagResponse createCertificate(@Valid @RequestBody CreateTagRequest tag) {
        try {
            return tagService.create(tag);
        } catch (ServiceException e) {
            throw new BadRequestException(ErrorCode.TAG_BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Deletes tag with given id.
     *
     * @param id id of desired tag.
     * @throws EntityNotFoundException if tag wasn't found.
     */
    @DeleteMapping(value = "/tags/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCertificate(@PathVariable int id) {
        try {
            tagService.delete(id);
        } catch (ServiceException e) {
            throw new EntityNotFoundException(ErrorCode.TAG_NOT_FOUND, "id=" + id);
        }
    }
}
