package com.code5150.meshgrouptest.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferRequest {

    @NotNull
    private Long toUserId;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal value;
}
