package com.epam.esm.web.controller;

import com.epam.esm.service.UserService;
import com.epam.esm.service.dto.response.UserResponse;
import com.epam.esm.service.exception.EntityNotFoundException;
import com.epam.esm.service.exception.ErrorCode;
import com.epam.esm.web.linker.UserLinker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@Validated
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
    private final UserService userService;
    private final UserLinker userLinker;
    private final PagedResourcesAssembler pagedResourcesAssembler;

    @Autowired
    public UserController(UserService userService, UserLinker userLinker, PagedResourcesAssembler pagedResourcesAssembler) {
        this.userService = userService;
        this.userLinker = userLinker;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    /**
     * Retrieves all gift users, that match given properties.
     *
     * @param pageable pagination restrictions.
     * @return list of found users.
     */
    @GetMapping(value = "/users")
    public PagedModel<UserResponse> findAllUsers(Pageable pageable) {
        Page<UserResponse> usersPage = userService.findAll(pageable);
        userLinker.processCollection(usersPage.getContent());
        return pagedResourcesAssembler.toModel(usersPage);
    }

    /**
     * Retrieves user with given id.
     *
     * @param id id of desired user.
     * @return found user.
     * @throws EntityNotFoundException if user wasn't found.
     */
    @GetMapping(value = "/users/{id}")
    public UserResponse findUser(@PathVariable int id) {
        Optional<UserResponse> response = userService.findById(id);
        response.ifPresent(userLinker::processEntity);
        return response.orElseThrow(() -> new EntityNotFoundException("id=" + id, ErrorCode.USER_NOT_FOUND));
    }
}
