package com.code5150.meshgrouptest.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank
    @Size(max = 500)
    private String name;

    @NotNull
    @Past
    private LocalDate dateOfBirth;

    @NotBlank
    @Size(min = 8, max = 500)
    private String password;

    @NotEmpty
    private List<@NotBlank @Size(max = 200) @Email String> emails;

    @NotEmpty
    private List<@NotBlank @Size(max = 13) String> phones;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal initialBalance;
}
