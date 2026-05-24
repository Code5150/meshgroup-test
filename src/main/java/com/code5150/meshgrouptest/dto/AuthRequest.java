package com.code5150.meshgrouptest.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthRequest {

    @Size(min = 8, max = 500)
    private String password;

    @Email
    @Size(max = 200)
    private String email;

    @Size(max = 13)
    private String phone;
}
