package com.epam.esm.web.controller;

import com.epam.esm.service.CertificateService;
import com.epam.esm.service.TagService;
import com.epam.esm.service.UserOrderService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.PagedModel;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.LinkedList;
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
    private final UserOrderService userOrderService;

    private final TagLinker tagPostProcessor;
    private final UserWithTagsLinker userWithTagsPostProcessor;
    private final CertificateLinker certificateLinker;
    private final PagedResourcesAssembler pagedResourcesAssembler;

    @Autowired
    public TagController(CertificateService certificateService, TagService tagService,
                         UserOrderService userOrderService, TagLinker tagPostProcessor, UserWithTagsLinker userWithTagsPostProcessor,
                         CertificateLinker certificateLinker, PagedResourcesAssembler pagedResourcesAssembler) {
        this.certificateService = certificateService;
        this.tagService = tagService;
        this.userOrderService = userOrderService;
        this.tagPostProcessor = tagPostProcessor;
        this.userWithTagsPostProcessor = userWithTagsPostProcessor;
        this.certificateLinker = certificateLinker;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    /**
     * Retrieves all tags.
     *
     * @param pageable pagination restrictions.
     * @return page of found tags.
     */
    @GetMapping(value = "/tags")
    public PagedModel<TagResponse> findAllTags(Pageable pageable) {
        Page<TagResponse> tagsPage = tagService.findAll(pageable);
        tagPostProcessor.processCollection(tagsPage.getContent());
        return pagedResourcesAssembler.toModel(tagsPage);
    }

    /**
     * Retrieves all certificates, assigned to given tag.
     *
     * @param pageable pagination restrictions.
     * @param id       id of tag.
     * @return list of found certificates.
     * @throws EntityNotFoundException if tag with given id wasn't found.
     */
    @GetMapping(value = "/tags/{id}/certificates")
    public PagedModel<CertificateItem> findCertificates(Pageable pageable, @PathVariable int id) {
        Page<CertificateItem> certificatesPage = certificateService.findAllByTagId(id, pageable);
        certificateLinker.processCollection(certificatesPage.getContent());
        return pagedResourcesAssembler.toModel(certificatesPage);
    }

    /**
     * Retrieves the most widely used tags of users with the highest cost of all orders.
     *
     * @return found users and corresponding tags.
     */
    @GetMapping(value = "/tags/theMostUsedTagsOfUsersWithTheHighestCost")
    public CollectionModel<? extends UserWithTags> findTheMostUsedTagsOfUsersWithTheHighestCost() {
        List<UserWithTags> list = new LinkedList<>();
        List<String> userIds = userOrderService.findUsersWithTheHighestCost();
        userIds.forEach(id -> list.add(new UserWithTags(id, tagService.findTheMostUsedTagsOfUser(id))));
        CollectionModel<? extends UserWithTags> response = userWithTagsPostProcessor.processCollection(list);
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
        Optional<TagResponse> response = tagService.findById(id);
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
    @RolesAllowed("admin")
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
    @RolesAllowed("admin")
    @DeleteMapping(value = "/tags/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteTag(@PathVariable int id) {
        tagService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
