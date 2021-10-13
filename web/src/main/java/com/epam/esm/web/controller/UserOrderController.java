package com.epam.esm.web.controller;

import com.epam.esm.service.UserOrderService;
import com.epam.esm.service.dto.request.CreateUserOrderRequest;
import com.epam.esm.service.dto.response.UserOrderItem;
import com.epam.esm.service.dto.response.UserOrderResponse;
import com.epam.esm.service.exception.ServiceException;
import com.epam.esm.web.exception.BadRequestException;
import com.epam.esm.web.exception.EntityNotFoundException;
import com.epam.esm.web.exception.ErrorCode;
import com.epam.esm.web.linker.UserOrderItemLinker;
import com.epam.esm.web.linker.UserOrderResponseLinker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Positive;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Validated
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class UserOrderController {
    private final UserOrderService orderService;

    private final UserOrderItemLinker userOrderItemPostProcessor;
    private final UserOrderResponseLinker userOrderResponsePostProcessor;

    @Autowired
    public UserOrderController(UserOrderService orderService, UserOrderItemLinker userOrderItemPostProcessor,
                               UserOrderResponseLinker userOrderResponsePostProcessor) {
        this.orderService = orderService;
        this.userOrderItemPostProcessor = userOrderItemPostProcessor;
        this.userOrderResponsePostProcessor = userOrderResponsePostProcessor;
    }

    /**
     * Retrieves all gift orders of user with given id.
     *
     * @param page   number of page.
     * @param size   size of page.
     * @param userId id of user.
     * @return list of found orders.
     * @throws BadRequestException if given parameters are invalid.
     */
    @GetMapping(value = "/users/{userId}/orders")
    public CollectionModel<? extends UserOrderItem> getAllOrdersByUserId(
            @RequestParam(defaultValue = "1")
            @Positive(message = "Page number must be a positive number") Integer page,
            @RequestParam(defaultValue = "5")
            @Positive(message = "Size must be a positive number") Integer size,
            @PathVariable Integer userId) {
        try {
            CollectionModel<? extends UserOrderItem> response = userOrderItemPostProcessor.processCollection(
                    orderService.findAllByUserId(page, size, userId));
            return response.add(linkTo(methodOn(UserOrderController.class)
                    .getAllOrdersByUserId(page, size, userId))
                    .withSelfRel());
        } catch (ServiceException e) {
            throw new BadRequestException(ErrorCode.USER_ORDER_BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Retrieves all gift orders on certificate with given id.
     *
     * @param page          number of page.
     * @param size          size of page.
     * @param certificateId id of certificate.
     * @return list of found orders.
     * @throws BadRequestException if given parameters are invalid.
     */
    @GetMapping(value = "/certificates/{certificateId}/orders")
    public CollectionModel<? extends UserOrderItem> getAllOrdersByCertificateId(
            @RequestParam(defaultValue = "1")
            @Positive(message = "Page number must be a positive number") Integer page,
            @RequestParam(defaultValue = "5")
            @Positive(message = "Size must be a positive number") Integer size,
            @PathVariable Integer certificateId) {
        try {
            CollectionModel<? extends UserOrderItem> response = userOrderItemPostProcessor.processCollection(
                    orderService.findAllByCertificateId(page, size, certificateId));
            return response.add(linkTo(methodOn(UserOrderController.class)
                    .getAllOrdersByCertificateId(page, size, certificateId))
                    .withSelfRel());
        } catch (ServiceException e) {
            throw new BadRequestException(ErrorCode.USER_ORDER_BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Retrieves order with given id.
     *
     * @param id id of desired order.
     * @return found order.
     * @throws EntityNotFoundException if order wasn't found.
     */
    @GetMapping(value = "/orders/{id}")
    public UserOrderResponse getOrder(@PathVariable int id) {
        Optional<UserOrderResponse> response = orderService.get(id);
        response.ifPresent(userOrderResponsePostProcessor::processEntity);
        return response.orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_ORDER_NOT_FOUND, "id=" + id));
    }

    /**
     * Creates new order from given {@link CreateUserOrderRequest}.
     *
     * @param order order to be created.
     * @return created order, if order is valid and service call was successful.
     * @throws BadRequestException if given order was invalid.
     */
    @PostMapping(value = "/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public UserOrderResponse createUserOrder(@RequestBody CreateUserOrderRequest order) {
        try {
            UserOrderResponse response = orderService.create(order);
            userOrderResponsePostProcessor.processEntity(response);
            return response;
        } catch (ServiceException e) {
            throw new BadRequestException(ErrorCode.USER_ORDER_BAD_REQUEST, e.getMessage());
        }
    }
}
