package com.code5150.meshgrouptest.controller;

import com.code5150.meshgrouptest.dto.UpdateUserRequest;
import com.code5150.meshgrouptest.dto.UserResponse;
import com.code5150.meshgrouptest.dto.UserSearchRequest;
import com.code5150.meshgrouptest.config.security.SecuritySchemes;
import com.code5150.meshgrouptest.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Users")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@SecurityRequirement(name = SecuritySchemes.BEARER_AUTH)
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get user by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public UserResponse getById(@Parameter(description = "User ID") @PathVariable Long id) {
        return userService.getUserResponseById(id);
    }

    @Operation(summary = "Search users by filters")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search results"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/search")
    public Page<UserResponse> search(@Valid UserSearchRequest request) {
        return userService.searchUsers(request);
    }

    @Operation(summary = "Update current user profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/me")
    public UserResponse updateMe(@Valid @RequestBody UpdateUserRequest request) {
        Long userId = getCurrentUserId();
        return userService.updateUser(userId, request);
    }

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}