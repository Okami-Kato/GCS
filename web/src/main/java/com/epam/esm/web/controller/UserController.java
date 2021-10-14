package com.epam.esm.web.controller;

import com.epam.esm.service.UserService;
import com.epam.esm.service.dto.response.UserResponse;
import com.epam.esm.service.exception.EntityNotFoundException;
import com.epam.esm.service.exception.ErrorCode;
import com.epam.esm.web.linker.UserLinker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Positive;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Validated
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
    private final UserService userService;

    private final UserLinker userPostProcessor;

    @Autowired
    public UserController(UserService userService, UserLinker userPostProcessor) {
        this.userService = userService;
        this.userPostProcessor = userPostProcessor;
    }

    /**
     * Retrieves all gift users, that match given properties.
     *
     * @param page number of page (starts from 1).
     * @param size size of page.
     * @return list of found users.
     * @throws IllegalArgumentException if pageNumber < 1, or pageSize < 0.
     */
    @GetMapping(value = "/users")
    public CollectionModel<? extends UserResponse> findAllUsers(
            @RequestParam(defaultValue = "1")
            @Positive(message = "Page must be a positive number") Integer page,
            @RequestParam(defaultValue = "5")
            @Positive(message = "Size must be a positive number") Integer size) {
        CollectionModel<? extends UserResponse> response = userPostProcessor.processCollection(userService.findAll(page, size));
        return response.add(linkTo(methodOn(UserController.class).findAllUsers(page, size)).withSelfRel());
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
        Optional<UserResponse> response = userService.find(id);
        response.ifPresent(userPostProcessor::processEntity);
        return response.orElseThrow(() -> new EntityNotFoundException("id=" + id, ErrorCode.USER_NOT_FOUND));
    }
}
