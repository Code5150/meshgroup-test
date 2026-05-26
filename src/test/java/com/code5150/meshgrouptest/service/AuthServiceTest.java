package com.code5150.meshgrouptest.service;

import com.code5150.meshgrouptest.dto.AuthRequest;
import com.code5150.meshgrouptest.dto.AuthResponse;
import com.code5150.meshgrouptest.dto.RegisterRequest;
import com.code5150.meshgrouptest.entity.EmailData;
import com.code5150.meshgrouptest.entity.PhoneData;
import com.code5150.meshgrouptest.entity.User;
import com.code5150.meshgrouptest.exception.InvalidCredentialsException;
import com.code5150.meshgrouptest.exception.UserAlreadyExistsException;
import com.code5150.meshgrouptest.repository.*;
import com.code5150.meshgrouptest.config.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private EmailDataRepository emailDataRepository;
    @Mock
    private PhoneDataRepository phoneDataRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    private static RegisterRequest buildRegisterRequest() {
        return RegisterRequest.builder()
                .name("Alice")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .password("password123")
                .emails(List.of("alice@example.com"))
                .phones(List.of("79201234567"))
                .initialBalance(new BigDecimal("100.00"))
                .build();
    }

    @Test
    void shouldRegisterSuccessfully() {
        RegisterRequest request = buildRegisterRequest();

        when(emailDataRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(phoneDataRepository.existsByPhone("79201234567")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded");

        assertDoesNotThrow(() -> authService.register(request));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowWhenEmailAlreadyTaken() {
        RegisterRequest request = buildRegisterRequest();
        when(emailDataRepository.existsByEmail("alice@example.com")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenPhoneAlreadyTaken() {
        RegisterRequest request = buildRegisterRequest();
        when(emailDataRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(phoneDataRepository.existsByPhone("79201234567")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldAuthenticateByEmail() {
        AuthRequest request = AuthRequest.builder()
                .email("alice@example.com")
                .password("password123")
                .build();

        User user = User.builder().id(1L).password("encoded").build();
        EmailData emailData = new EmailData(1L, user, "alice@example.com");

        when(emailDataRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(emailData));
        when(passwordEncoder.matches("password123", "encoded")).thenReturn(true);
        when(jwtTokenProvider.generateToken(1L)).thenReturn("jwt-token");

        AuthResponse response = authService.authenticate(request);

        assertEquals("jwt-token", response.getToken());
    }

    @Test
    void shouldThrowWhenPasswordWrong() {
        AuthRequest request = AuthRequest.builder()
                .email("alice@example.com")
                .password("wrong")
                .build();

        User user = User.builder().id(1L).password("encoded").build();
        EmailData emailData = new EmailData(1L, user, "alice@example.com");

        when(emailDataRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(emailData));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.authenticate(request));
    }

    @Test
    void shouldAuthenticateByPhone() {
        AuthRequest request = AuthRequest.builder()
                .phone("79201234567")
                .password("password123")
                .build();

        User user = User.builder().id(1L).password("encoded").build();
        PhoneData phoneData = new PhoneData(1L, user, "79201234567");

        when(phoneDataRepository.findByPhone("79201234567")).thenReturn(Optional.of(phoneData));
        when(passwordEncoder.matches("password123", "encoded")).thenReturn(true);
        when(jwtTokenProvider.generateToken(1L)).thenReturn("jwt-phone-token");

        AuthResponse response = authService.authenticate(request);

        assertEquals("jwt-phone-token", response.getToken());
    }

    @Test
    void shouldThrowWhenUserNotFoundByEmail() {
        AuthRequest request = AuthRequest.builder()
                .email("unknown@example.com")
                .password("password123")
                .build();

        when(emailDataRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authService.authenticate(request));
    }

    @Test
    void shouldThrowWhenUserNotFoundByPhone() {
        AuthRequest request = AuthRequest.builder()
                .phone("79201234567")
                .password("password123")
                .build();

        when(phoneDataRepository.findByPhone("79201234567")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authService.authenticate(request));
    }
}
