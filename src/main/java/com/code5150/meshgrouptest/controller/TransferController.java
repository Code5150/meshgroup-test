package com.code5150.meshgrouptest.controller;

import com.code5150.meshgrouptest.dto.TransferRequest;
import com.code5150.meshgrouptest.service.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfer")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    public void transfer(@Valid @RequestBody TransferRequest request) {
        Long fromUserId = getCurrentUserId();
        transferService.transfer(fromUserId, request);
    }

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}