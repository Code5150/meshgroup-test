package com.code5150.meshgrouptest.controller;

import com.code5150.meshgrouptest.dto.TransferRequest;
import com.code5150.meshgrouptest.security.SecuritySchemes;
import com.code5150.meshgrouptest.service.AccountService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfer")
@RequiredArgsConstructor
@SecurityRequirement(name = SecuritySchemes.BEARER_AUTH)
public class TransferController {

    private final AccountService accountService;

    @PostMapping
    public void transfer(@Valid @RequestBody TransferRequest request) {
        Long fromUserId = getCurrentUserId();
        accountService.transfer(fromUserId, request);
    }

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}