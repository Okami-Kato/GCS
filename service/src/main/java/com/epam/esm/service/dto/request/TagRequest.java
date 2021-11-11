package com.epam.esm.service.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class TagRequest {
    @NotBlank(message = "Tag name can't be blank")
    @Size(min = 3, max = 25, message = "Tag name must be {min}-{max} characters long.")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Tag name can contain only letters.")
    private String name;
}
