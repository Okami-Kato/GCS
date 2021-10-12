package com.epam.esm.service.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class CreateUserRequest {
    @Size(min = 2, max = 25, message = "User first name must be {min}-{max} characters long.")
    @NotBlank(message = "User first name can't be blank.")
    @Pattern(regexp = "^[A-Z][a-z]+$", message = "User first name can contain letters, must start with an uppercase letter.")
    private String firstName;

    @Size(min = 2, max = 25, message = "User first name must be {min}-{max} characters long.")
    @NotBlank(message = "User first name can't be blank.")
    @Pattern(regexp = "^([A-Z][a-z ,.'-]+)+$", message = "User last name can contain letters and special characters \"., '-\", must start with an uppercase letter.")
    private String lastName;

    @Size(min = 2, max = 25, message = "User login must be {min}-{max} characters long.")
    @NotBlank(message = "User login can't be blank.")
    private String login;

    @Size(min = 2, max = 25, message = "User password must be {min}-{max} characters long.")
    @NotBlank(message = "User password can't be blank.")
    private String password;

    public CreateUserRequest() {
    }

    public CreateUserRequest(String firstName, String lastName, String login, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.login = login;
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "CreateUserRequest{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
