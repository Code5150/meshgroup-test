package com.code5150.meshgrouptest.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSearchRequest {

    private LocalDate dateOfBirth;

    @Size(max = 500)
    private String name;

    @Pattern(regexp = "\\d*")
    @Size(min = 7, max = 13)
    private String phone;

    @Email
    @Size(max = 200)
    private String email;

    @Min(0)
    @Builder.Default
    private int page = 0;

    @Min(1)
    @Max(100)
    @Builder.Default
    private int size = 20;
}
