package com.code5150.meshgrouptest.controller;

import com.code5150.meshgrouptest.dto.TransferRequest;
import com.code5150.meshgrouptest.security.SecuritySchemes;
import com.code5150.meshgrouptest.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Transfer")
@RestController
@RequestMapping("/api/transfer")
@RequiredArgsConstructor
@SecurityRequirement(name = SecuritySchemes.BEARER_AUTH)
public class TransferController {

    private final AccountService accountService;

    @Operation(summary = "Transfer money to another user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transfer completed"),
            @ApiResponse(responseCode = "400", description = "Insufficient funds or invalid request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Receiver not found")
    })
    @PostMapping
    public void transfer(@Valid @RequestBody TransferRequest request) {
        Long fromUserId = getCurrentUserId();
        accountService.transfer(fromUserId, request);
    }

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}