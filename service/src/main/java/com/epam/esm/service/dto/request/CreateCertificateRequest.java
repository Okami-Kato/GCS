package com.epam.esm.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CreateCertificateRequest {
    @Size(min = 3, max = 50, message = "Certificate name must be {min}-{max} characters long.")
    @NotBlank(message = "Certificate name can't be blank.")
    @Pattern(regexp = "^[\\w\\s]+$", message = "Certificate name must be alphanumeric.")
    private String name;

    @Size(min = 10, max = 3000, message = "Certificate description must be {min}-{max} characters long.")
    @NotBlank(message = "Certificate description can't be blank.")
    @Pattern(regexp = "^[\\w\\s]+$", message = "Certificate description must be alphanumeric.")
    private String description;

    @NotNull
    @Positive(message = "Certificate price must be positive number.")
    private Integer price;

    @NotNull
    @Positive(message = "Certificate duration must be positive number.")
    private Integer duration;

    private Set<@Valid TagRequest> tags;
}
