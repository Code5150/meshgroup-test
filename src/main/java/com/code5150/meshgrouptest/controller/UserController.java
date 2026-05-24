package com.code5150.meshgrouptest.controller;

import com.code5150.meshgrouptest.dto.UpdateUserRequest;
import com.code5150.meshgrouptest.dto.UserResponse;
import com.code5150.meshgrouptest.dto.UserSearchRequest;
import com.code5150.meshgrouptest.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
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
    public void updateMe(@Valid @RequestBody UpdateUserRequest request) {
        Long userId = getCurrentUserId();
        userService.updateUser(userId, request);
    }

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}