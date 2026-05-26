package com.code5150.meshgrouptest.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    @Size(max = 500)
    private String name;

    @Past
    private LocalDate dateOfBirth;

    @Size(min = 8, max = 500)
    private String password;

    @NotEmpty
    private List<@NotBlank @Size(max = 200) @Email String> emails;

    @NotEmpty
    private List<@NotBlank @Size(min = 7, max = 13) @Pattern(regexp = "\\d*") String> phones;
}
