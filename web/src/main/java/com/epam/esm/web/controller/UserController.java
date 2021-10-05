package com.epam.esm.web.controller;

import com.epam.esm.service.UserService;
import com.epam.esm.service.dto.response.UserResponse;
import com.epam.esm.service.exception.ServiceException;
import com.epam.esm.web.exception.BadRequestException;
import com.epam.esm.web.exception.EntityNotFoundException;
import com.epam.esm.web.exception.ErrorCode;
import com.epam.esm.web.processor.UserPostProcessor;
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

    private final UserPostProcessor userPostProcessor;

    @Autowired
    public UserController(UserService userService, UserPostProcessor userPostProcessor) {
        this.userService = userService;
        this.userPostProcessor = userPostProcessor;
    }

    /**
     * Retrieves all gift users, that match given properties.
     *
     * @param page number of page.
     * @param size size of page.
     * @return list of found users.
     * @throws BadRequestException if given parameters are invalid.
     */
    @GetMapping(value = "/users")
    public CollectionModel<? extends UserResponse> getAllUsers(@RequestParam(defaultValue = "1")
                                                               @Positive(message = "Page number must be a positive number") Integer page,
                                                               @RequestParam(defaultValue = "5")
                                                               @Positive(message = "Size must be a positive number") Integer size) {
        try {
            CollectionModel<? extends UserResponse> response = userPostProcessor.processCollection(userService.getAll(page, size));
            return response.add(linkTo(methodOn(UserController.class).getAllUsers(page, size)).withSelfRel());
        } catch (ServiceException e) {
            throw new BadRequestException(ErrorCode.USER_BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Retrieves user with given id.
     *
     * @param id id of desired user.
     * @return found user.
     * @throws EntityNotFoundException if user wasn't found.
     */
    @GetMapping(value = "/users/{id}")
    public UserResponse getUser(@PathVariable int id) {
        Optional<UserResponse> response = userService.get(id);
        response.ifPresent(userPostProcessor::processEntity);
        return response.orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND, "id=" + id));
    }
}
