package com.code5150.meshgrouptest.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    @NotEmpty
    private List<@NotBlank @Size(max = 200) @Email String> emails;

    @NotEmpty
    private List<@NotBlank @Size(min = 7, max = 13) @Pattern(regexp = "\\d*") String> phones;
}
