package com.code5150.meshgrouptest.controller;

import com.code5150.meshgrouptest.BaseIntegrationTest;
import com.code5150.meshgrouptest.dto.AuthRequest;
import com.code5150.meshgrouptest.dto.RegisterRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerIT extends BaseIntegrationTest {

    private static final String REGISTER_URL = "/api/auth/register";
    private static final String LOGIN_URL = "/api/auth/login";

    @Test
    void shouldRegisterNewUser() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .name("Alice")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .password("password123")
                .emails(List.of("alice@example.com"))
                .phones(List.of("79201234567"))
                .initialBalance(new BigDecimal("100.00"))
                .build();

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldFailWhenDuplicateEmail() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .name("Alice")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .password("password123")
                .emails(List.of("alice@example.com"))
                .phones(List.of("79201234567"))
                .initialBalance(new BigDecimal("100.00"))
                .build();

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // duplicate email, different phone
        RegisterRequest duplicate = RegisterRequest.builder()
                .name("Bob")
                .dateOfBirth(LocalDate.of(1995, 10, 20))
                .password("password456")
                .emails(List.of("alice@example.com"))
                .phones(List.of("79209876543"))
                .initialBalance(new BigDecimal("200.00"))
                .build();

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicate)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldLoginWithValidCredentials() throws Exception {
        registerAndLogin("Alice", "alice@example.com", "79201234567", "password123",
                new BigDecimal("100.00"));

        AuthRequest authRequest = AuthRequest.builder()
                .email("alice@example.com")
                .password("password123")
                .build();

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", not(emptyOrNullString())));
    }

    @Test
    void shouldFailWhenWrongPassword() throws Exception {
        registerAndLogin("Alice", "alice@example.com", "79201234567", "password123",
                new BigDecimal("100.00"));

        AuthRequest authRequest = AuthRequest.builder()
                .email("alice@example.com")
                .password("wrongpassword")
                .build();

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized());
    }
}
