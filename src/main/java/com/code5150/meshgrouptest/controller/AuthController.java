package com.code5150.meshgrouptest.controller;

import com.code5150.meshgrouptest.dto.AuthRequest;
import com.code5150.meshgrouptest.dto.AuthResponse;
import com.code5150.meshgrouptest.dto.RegisterRequest;
import com.code5150.meshgrouptest.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest request) {
        return authService.authenticate(request);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
    }
}