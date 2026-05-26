package com.code5150.meshgrouptest.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthRequest {

    @Email
    @Size(max = 200)
    private String email;

    @Pattern(regexp = "\\d*")
    @Size(min = 7, max = 13)
    private String phone;

    @Size(min = 8, max = 500)
    private String password;
}
