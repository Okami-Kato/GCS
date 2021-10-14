package com.epam.esm.web.controller;

import com.epam.esm.service.CertificateService;
import com.epam.esm.service.TagService;
import com.epam.esm.service.dto.request.TagRequest;
import com.epam.esm.service.dto.response.CertificateItem;
import com.epam.esm.service.dto.response.TagResponse;
import com.epam.esm.service.dto.response.UserWithTags;
import com.epam.esm.service.exception.EntityExistsException;
import com.epam.esm.service.exception.EntityNotFoundException;
import com.epam.esm.service.exception.ErrorCode;
import com.epam.esm.service.exception.InvalidEntityException;
import com.epam.esm.web.linker.CertificateLinker;
import com.epam.esm.web.linker.TagLinker;
import com.epam.esm.web.linker.UserWithTagsLinker;
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

    private final TagLinker tagPostProcessor;
    private final UserWithTagsLinker userWithTagsPostProcessor;
    private final CertificateLinker certificateLinker;

    @Autowired
    public TagController(CertificateService certificateService, TagService tagService,
                         TagLinker tagPostProcessor, UserWithTagsLinker userWithTagsPostProcessor,
                         CertificateLinker certificateLinker) {
        this.certificateService = certificateService;
        this.tagService = tagService;
        this.tagPostProcessor = tagPostProcessor;
        this.userWithTagsPostProcessor = userWithTagsPostProcessor;
        this.certificateLinker = certificateLinker;
    }

    /**
     * Retrieves all tags.
     *
     * @param page number of page (starts from 1).
     * @param size size of page.
     * @return list of found tags.
     * @throws IllegalArgumentException if pageNumber < 1, or pageSize < 0.
     */
    @GetMapping(value = "/tags")
    public CollectionModel<? extends TagResponse> findAllTags(
            @RequestParam(defaultValue = "1")
            @Positive(message = "Page must be a positive number") Integer page,
            @RequestParam(defaultValue = "5")
            @Positive(message = "Size must be a positive number") Integer size) {
        List<TagResponse> response = tagService.findAll(page, size);
        CollectionModel<? extends TagResponse> result = tagPostProcessor.processCollection(response);
        return result.add(linkTo(methodOn(TagController.class).findAllTags(page, size)).withSelfRel());
    }

    /**
     * Retrieves all certificates, assigned to given tag.
     *
     * @param page number of page (starts from 1).
     * @param size size of page.
     * @param id   id of tag.
     * @return list of found certificates.
     * @throws IllegalArgumentException if pageNumber < 1, or pageSize < 0.
     * @throws EntityNotFoundException  if tag with given id wasn't found.
     */
    @GetMapping(value = "/tags/{id}/certificates")
    public CollectionModel<? extends CertificateItem> findCertificates(
            @RequestParam(defaultValue = "1")
            @Positive(message = "Page must be a positive number") Integer page,
            @RequestParam(defaultValue = "5")
            @Positive(message = "Size must be a positive number") Integer size,
            @PathVariable int id) {
        List<CertificateItem> response = certificateService.findAllByTagId(page, size, id);
        CollectionModel<? extends CertificateItem> certificates = certificateLinker.processCollection(response);
        return certificates.add(linkTo(methodOn(TagController.class).findCertificates(page, size, id)).withSelfRel());
    }

    /**
     * Retrieves the most widely used tags of users with the highest cost of all orders.
     *
     * @return found users and corresponding tags.
     */
    @GetMapping(value = "/tags/theMostUsedTagsOfUsersWithTheHighestCost")
    public CollectionModel<? extends UserWithTags> findTheMostUsedTagsOfUsersWithTheHighestCost() {
        CollectionModel<? extends UserWithTags> response = userWithTagsPostProcessor.processCollection(
                tagService.findTheMostUsedTagsOfUsersWithTheHighestCost());
        return response.add(linkTo(methodOn(TagController.class)
                .findTheMostUsedTagsOfUsersWithTheHighestCost())
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
    public TagResponse findTag(@PathVariable int id) {
        Optional<TagResponse> response = tagService.find(id);
        response.ifPresent(tagPostProcessor::processEntity);
        return response.orElseThrow(() -> new EntityNotFoundException("id=" + id, ErrorCode.TAG_NOT_FOUND));
    }

    /**
     * Creates new tag from given {@link TagRequest}.
     *
     * @param tag tag to be created.
     * @return created tag, if tag is valid and service call was successful.
     * @throws IllegalArgumentException if tag is null.
     * @throws InvalidEntityException   if tag is invalid.
     * @throws EntityExistsException    if tag with the same name already exists.
     */
    @PostMapping(value = "/tags")
    @ResponseStatus(HttpStatus.CREATED)
    public TagResponse createTag(@Valid @RequestBody TagRequest tag) {
        TagResponse response = tagService.create(tag);
        tagPostProcessor.processEntity(response);
        return response;
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
        tagService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
