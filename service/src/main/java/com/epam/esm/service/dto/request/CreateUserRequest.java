package com.epam.esm.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {
    @Size(min = 2, max = 50, message = "Full name must be {min}-{max} characters long.")
    @NotBlank(message = "Full name can't be blank.")
    @Pattern(regexp = "^[A-Z][a-z]+$", message = "Full name can contain letters, must start with an uppercase letter.")
    private String fullName;

    @ToString.Exclude
    @Size(min = 2, max = 50, message = "Username must be {min}-{max} characters long.")
    @NotBlank(message = "Username can't be blank.")
    private String username;

    @ToString.Exclude
    @Size(min = 2, max = 50, message = "Password must be {min}-{max} characters long.")
    @NotBlank(message = "Password can't be blank.")
    private String password;
}
