package com.epam.esm.web.linker;

import com.epam.esm.service.dto.response.UserResponse;
import com.epam.esm.web.controller.UserController;
import com.epam.esm.web.controller.UserOrderController;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserLinker implements RepresentationModelLinker<UserResponse> {
    @Override
    public void processEntity(UserResponse entity) {
        entity.add(linkTo(methodOn(UserController.class).getUser(entity.getId())).withSelfRel());
        entity.add(linkTo(methodOn(UserOrderController.class)
                .getAllOrdersByUserId(null, null, entity.getId()))
                .withRel("ordersOfUser"));
        entity.add(linkTo(methodOn(UserController.class).getAllUsers(null, null)).withRel("allUsers"));
    }
}
