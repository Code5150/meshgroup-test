package com.code5150.meshgrouptest.controller;

import com.code5150.meshgrouptest.BaseIntegrationTest;
import com.code5150.meshgrouptest.dto.TransferRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TransferControllerIT extends BaseIntegrationTest {

    @Test
    void shouldTransferMoneyBetweenUsers() throws Exception {
        String senderToken = registerAndLogin("Alice", "alice@example.com", "79201234567",
                "password123", new BigDecimal("200.00"));
        registerAndLogin("Bob", "bob@example.com", "79209876543",
                "password456", new BigDecimal("50.00"));

        // Bob is user #2
        TransferRequest transferRequest = new TransferRequest(2L, new BigDecimal("75.00"));

        mockMvc.perform(post("/api/transfer")
                        .header("Authorization", "Bearer " + senderToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isOk());

        // Verify sender balance decreased by 75
        mockMvc.perform(get("/api/users/1")
                        .header("Authorization", "Bearer " + senderToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance", is(125.0)));

        // Login as Bob and verify his balance increased
        String bobToken = login("bob@example.com", null, "password456");
        mockMvc.perform(get("/api/users/2")
                        .header("Authorization", "Bearer " + bobToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance", is(125.0)));
    }

    @Test
    void shouldFailWhenTransferToSelf() throws Exception {
        String token = registerAndLogin("Alice", "alice@example.com", "79201234567",
                "password123", new BigDecimal("200.00"));

        TransferRequest transferRequest = new TransferRequest(1L, new BigDecimal("50.00"));

        mockMvc.perform(post("/api/transfer")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailWhenInsufficientFunds() throws Exception {
        String token = registerAndLogin("Alice", "alice@example.com", "79201234567",
                "password123", new BigDecimal("50.00"));
        registerAndLogin("Bob", "bob@example.com", "79209876543",
                "password456", new BigDecimal("50.00"));

        TransferRequest transferRequest = new TransferRequest(2L, new BigDecimal("100.00"));

        mockMvc.perform(post("/api/transfer")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailWhenReceiverNotFound() throws Exception {
        String token = registerAndLogin("Alice", "alice@example.com", "79201234567",
                "password123", new BigDecimal("200.00"));

        TransferRequest transferRequest = new TransferRequest(9999L, new BigDecimal("50.00"));

        mockMvc.perform(post("/api/transfer")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isNotFound());
    }
}
