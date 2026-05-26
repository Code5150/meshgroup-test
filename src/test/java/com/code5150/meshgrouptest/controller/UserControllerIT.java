package com.code5150.meshgrouptest.controller;

import com.code5150.meshgrouptest.BaseIntegrationTest;
import com.code5150.meshgrouptest.dto.UpdateUserRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerIT extends BaseIntegrationTest {

    @Test
    void shouldGetUserById() throws Exception {
        String token = registerAndLogin("Alice", "alice@example.com", "79201234567",
                "password123", new BigDecimal("100.00"));

        mockMvc.perform(get("/api/users/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Alice")))
                .andExpect(jsonPath("$.emails[0]", is("alice@example.com")));
    }

    @Test
    void shouldReturn404ForNonExistingUser() throws Exception {
        String token = registerAndLogin("Alice", "alice@example.com", "79201234567",
                "password123", new BigDecimal("100.00"));

        mockMvc.perform(get("/api/users/9999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldSearchUsersByName() throws Exception {
        String token = registerAndLogin("Alice", "alice@example.com", "79201234567",
                "password123", new BigDecimal("100.00"));

        mockMvc.perform(get("/api/users/search")
                        .param("name", "Ali")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[0].name", is("Alice")));
    }

    @Test
    void shouldSearchUsersByEmail() throws Exception {
        String token = registerAndLogin("Alice", "alice@example.com", "79201234567",
                "password123", new BigDecimal("100.00"));

        mockMvc.perform(get("/api/users/search")
                        .param("email", "alice@example.com")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("Alice")));
    }

    @Test
    void shouldUpdateCurrentUserProfile() throws Exception {
        String token = registerAndLogin("Alice", "alice@example.com", "79201234567",
                "password123", new BigDecimal("100.00"));

        UpdateUserRequest updateRequest = new UpdateUserRequest(
                "Alice Updated", null, null,
                List.of("alice@example.com"),
                List.of("79201234567")
        );

        mockMvc.perform(put("/api/users/me")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Alice Updated")));
    }
}
