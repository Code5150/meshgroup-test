package com.code5150.meshgrouptest.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRequest {

    @NotEmpty
    private List<@NotBlank @Size(max = 200) @Email String> emails;

    @NotEmpty
    private List<@NotBlank @Size(max = 13) String> phones;
}
