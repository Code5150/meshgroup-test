package com.code5150.meshgrouptest.controller;

import com.code5150.meshgrouptest.dto.UpdateUserRequest;
import com.code5150.meshgrouptest.dto.UserResponse;
import com.code5150.meshgrouptest.dto.UserSearchRequest;
import com.code5150.meshgrouptest.security.SecuritySchemes;
import com.code5150.meshgrouptest.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@SecurityRequirement(name = SecuritySchemes.BEARER_AUTH)
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable Long id) {
        return userService.getUserResponseById(id);
    }

    @GetMapping("/search")
    public Page<UserResponse> search(@Valid UserSearchRequest request) {
        return userService.searchUsers(request);
    }

    @PutMapping("/me")
    public UserResponse updateMe(@Valid @RequestBody UpdateUserRequest request) {
        Long userId = getCurrentUserId();
        return userService.updateUser(userId, request);
    }

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}