package com.code5150.meshgrouptest;

import com.code5150.meshgrouptest.dto.AuthRequest;
import com.code5150.meshgrouptest.dto.AuthResponse;
import com.code5150.meshgrouptest.dto.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
public abstract class BaseIntegrationTest {

    private static final String REGISTER_URL = "/api/auth/register";
    private static final String LOGIN_URL = "/api/auth/login";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("classpath:cleanup.sql")
    private Resource cleanupScript;

    protected final ObjectMapper objectMapper = new ObjectMapper();
    {
        objectMapper.findAndRegisterModules();
    }

    @BeforeEach
    void cleanDatabase() {
        try {
            String sql = StreamUtils.copyToString(cleanupScript.getInputStream(), StandardCharsets.UTF_8);
            jdbcTemplate.execute(sql);
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute cleanup script", e);
        }
    }

    /**
     * Registers a user and returns the JWT token from subsequent login.
     */
    protected String registerAndLogin(String name, String email, String phone, String password,
                                       BigDecimal initialBalance) throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .name(name)
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .password(password)
                .emails(List.of(email))
                .phones(List.of(phone))
                .initialBalance(initialBalance)
                .build();

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        return login(email, null, password);
    }

    /**
     * Performs login and returns the JWT token.
     */
    protected String login(String email, String phone, String password) throws Exception {
        AuthRequest authRequest = AuthRequest.builder()
                .email(email)
                .phone(phone)
                .password(password)
                .build();

        MvcResult result = mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponse authResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(), AuthResponse.class);
        return authResponse.getToken();
    }
}