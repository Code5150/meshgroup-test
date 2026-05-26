package com.code5150.meshgrouptest.service;

import com.code5150.meshgrouptest.dto.UpdateUserRequest;
import com.code5150.meshgrouptest.dto.UserResponse;
import com.code5150.meshgrouptest.dto.UserSearchRequest;
import com.code5150.meshgrouptest.entity.Account;
import com.code5150.meshgrouptest.entity.EmailData;
import com.code5150.meshgrouptest.entity.PhoneData;
import com.code5150.meshgrouptest.entity.User;
import com.code5150.meshgrouptest.exception.ResourceNotFoundException;
import com.code5150.meshgrouptest.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private EmailDataRepository emailDataRepository;

    @InjectMocks
    private UserService userService;

    private User createTestUser() {
        User user = User.builder()
                .id(1L)
                .name("Alice")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .password("encoded")
                .build();

        EmailData emailData = new EmailData(1L, user, "alice@example.com");
        PhoneData phoneData = new PhoneData(1L, user, "79201234567");
        user.setEmails(new ArrayList<>(List.of(emailData)));
        user.setPhones(new ArrayList<>(List.of(phoneData)));

        Account account = Account.builder()
                .id(1L)
                .user(user)
                .balance(new BigDecimal("100.00"))
                .initialBalance(new BigDecimal("100.00"))
                .build();
        user.setAccount(account);

        return user;
    }

    @Test
    void shouldReturnUserById() {
        User user = createTestUser();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponse response = userService.getUserResponseById(1L);

        assertEquals("Alice", response.getName());
        assertEquals(1, response.getEmails().size());
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserResponseById(99L));
    }

    @Test
    void shouldSearchUsersByName() {
        User user = createTestUser();
        Page<User> userPage = new PageImpl<>(List.of(user));

        when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(userPage);

        UserSearchRequest request = UserSearchRequest.builder().name("Ali").page(0).size(20).build();
        Page<UserResponse> result = userService.searchUsers(request);

        assertEquals(1, result.getTotalElements());
        assertEquals("Alice", result.getContent().getFirst().getName());
    }

    @Test
    void shouldUpdateUserEmails() {
        User user = createTestUser();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UpdateUserRequest request = new UpdateUserRequest(
                null, null, null,
                List.of("alice@example.com", "alice2@example.com"),
                List.of("79201234567")
        );

        when(emailDataRepository.existsByEmail("alice2@example.com")).thenReturn(false);

        UserResponse response = userService.updateUser(1L, request);

        assertEquals(2, response.getEmails().size());
    }

    @Test
    void shouldThrowWhenRemovingLastEmail() {
        User user = createTestUser();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UpdateUserRequest request = new UpdateUserRequest(
                null, null, null,
                Collections.emptyList(),
                List.of("79201234567")
        );

        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(1L, request));
    }

    @Test
    void shouldThrowWhenRemovingLastPhone() {
        User user = createTestUser();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UpdateUserRequest request = new UpdateUserRequest(
                null, null, null,
                List.of("alice2@example.com"),
                Collections.emptyList()
        );

        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(1L, request));
    }
}