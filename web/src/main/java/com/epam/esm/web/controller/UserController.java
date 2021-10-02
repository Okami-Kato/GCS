package com.epam.esm.web.controller;

import com.epam.esm.service.UserService;
import com.epam.esm.service.dto.response.CertificateResponse;
import com.epam.esm.service.dto.response.UserResponse;
import com.epam.esm.service.exception.ServiceException;
import com.epam.esm.util.CertificateFilter;
import com.epam.esm.util.Sort;
import com.epam.esm.util.SortDirection;
import com.epam.esm.web.exception.BadRequestException;
import com.epam.esm.web.exception.EntityNotFoundException;
import com.epam.esm.web.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Positive;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@RestController
@Validated
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves all gift users, that match given properties.
     *
     * @param page            number of page.
     * @param size            size of page.
     * @return list of found users.
     * @throws BadRequestException if given parameters are invalid.
     */
    @GetMapping(value = "/users")
    public List<UserResponse> getAllUsers(@RequestParam(defaultValue = "1")
                                          @Positive(message = "Page number must be a positive number") int page,
                                          @RequestParam(defaultValue = "5")
                                          @Positive(message = "Size must be a positive number") int size) {
        try {
            return userService.getAll(page, size);
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
        Optional<UserResponse> user = userService.get(id);
        return user.orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND, "id=" + id));
    }
}
