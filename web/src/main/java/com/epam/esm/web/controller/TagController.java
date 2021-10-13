package com.epam.esm.web.controller;

import com.epam.esm.service.CertificateService;
import com.epam.esm.service.TagService;
import com.epam.esm.service.dto.request.CreateTagRequest;
import com.epam.esm.service.dto.response.CertificateItem;
import com.epam.esm.service.dto.response.TagResponse;
import com.epam.esm.service.dto.response.UserWithTags;
import com.epam.esm.service.exception.ServiceException;
import com.epam.esm.util.CertificateFilter;
import com.epam.esm.web.exception.BadRequestException;
import com.epam.esm.web.exception.EntityNotFoundException;
import com.epam.esm.web.exception.ErrorCode;
import com.epam.esm.web.processor.CertificatePostProcessor;
import com.epam.esm.web.processor.TagPostProcessor;
import com.epam.esm.web.processor.UserPostProcessor;
import com.epam.esm.web.processor.UserWithTagsPostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Validated
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class TagController {
    private final CertificateService certificateService;
    private final TagService tagService;

    private final TagPostProcessor tagPostProcessor;
    private final UserPostProcessor userPostProcessor;
    private final UserWithTagsPostProcessor userWithTagsPostProcessor;
    private final CertificatePostProcessor certificatePostProcessor;

    @Autowired
    public TagController(CertificateService certificateService, TagService tagService,
                         TagPostProcessor tagPostProcessor, UserPostProcessor userPostProcessor,
                         UserWithTagsPostProcessor userWithTagsPostProcessor,
                         CertificatePostProcessor certificatePostProcessor) {
        this.certificateService = certificateService;
        this.tagService = tagService;
        this.tagPostProcessor = tagPostProcessor;
        this.userPostProcessor = userPostProcessor;
        this.userWithTagsPostProcessor = userWithTagsPostProcessor;
        this.certificatePostProcessor = certificatePostProcessor;
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
    public CollectionModel<? extends TagResponse> getAllTags(
            @RequestParam(defaultValue = "1")
            @Positive(message = "Page number must be a positive number") Integer page,
            @RequestParam(defaultValue = "5")
            @Positive(message = "Size must be a positive number") Integer size) {
        try {
            List<TagResponse> response = tagService.findAllByCertificateId(page, size);
            CollectionModel<? extends TagResponse> result = tagPostProcessor.processCollection(response);
            return result.add(linkTo(methodOn(TagController.class).getAllTags(page, size)).withSelfRel());
        } catch (ServiceException e) {
            throw new BadRequestException(ErrorCode.TAG_BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Retrieves all certificates, assigned to given tag.
     *
     * @param page number of page.
     * @param size size of page.
     * @param id   id of tag.
     * @return list of found certificates.
     * @throws BadRequestException if given parameters are invalid.
     */
    @GetMapping(value = "/tags/{id}/certificates")
    public CollectionModel<? extends CertificateItem> getCertificates(
            @RequestParam(defaultValue = "1")
            @Positive(message = "Page number must be a positive number") Integer page,
            @RequestParam(defaultValue = "5")
            @Positive(message = "Size must be a positive number") Integer size,
            @PathVariable int id) {
        try {
            List<CertificateItem> response = certificateService.findAllWithFilter(page, size, CertificateFilter.newBuilder().withTags(id).build());
            CollectionModel<? extends CertificateItem> certificates = certificatePostProcessor.processCollection(response);
            return certificates.add(linkTo(methodOn(TagController.class).getCertificates(page, size, id)).withSelfRel());
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
    @GetMapping(value = "/tags/theMostUsedTagsOfUsersWithTheHighestCost")
    public CollectionModel<? extends UserWithTags> getTheMostUsedTagsOfUsersWithTheHighestCost() {
        CollectionModel<? extends UserWithTags> response = userWithTagsPostProcessor.processCollection(
                tagService.getTheMostUsedTagsOfUsersWithTheHighestCost());
        return response.add(linkTo(methodOn(TagController.class)
                .getTheMostUsedTagsOfUsersWithTheHighestCost())
                .withSelfRel());
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
        Optional<TagResponse> response = tagService.get(id);
        response.ifPresent(tagPostProcessor::processEntity);
        return response.orElseThrow(() -> new EntityNotFoundException(ErrorCode.TAG_NOT_FOUND, "id=" + id));
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
    public TagResponse createTag(@Valid @RequestBody CreateTagRequest tag) {
        try {
            TagResponse response = tagService.create(tag);
            tagPostProcessor.processEntity(response);
            return response;
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
    public ResponseEntity<Void> deleteTag(@PathVariable int id) {
        try {
            tagService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (ServiceException e) {
            throw new EntityNotFoundException(ErrorCode.TAG_NOT_FOUND, "id=" + id);
        }
    }
}
