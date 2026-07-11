package com.accountshield.controller;

import com.accountshield.common.ApiResponse;
import com.accountshield.dto.admin.AdminUserResponse;
import com.accountshield.dto.admin.ChangeRoleRequest;
import com.accountshield.dto.admin.ChangeStatusRequest;
import com.accountshield.service.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(
        name = "Administration",
        description = "Admin user management"
)
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @Operation(summary = "Get all users")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<AdminUserResponse>>> getUsers(Pageable pageable) {

        Page<AdminUserResponse> response = adminUserService.getUsers(pageable);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Users fetched successfully",  response));

    }

    @Operation(summary = "Get user by id")
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<AdminUserResponse>> getUserById(
            @PathVariable UUID userId
    ) {

        AdminUserResponse response = adminUserService.getUserById(userId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("User fetched successfully",  response));

    }

    @Operation(summary = "Assign a role to user")
    @PatchMapping("/{userId}/role")
    public ResponseEntity<ApiResponse<AdminUserResponse>> assignRole(
            @PathVariable UUID userId,
            @RequestBody ChangeRoleRequest request
            ) {

        AdminUserResponse response = adminUserService.assignRole(userId, request);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Role assigned to user ",  response));

    }

    @Operation(summary = "Change user status")
    @PatchMapping("/{userId}/status")
    public ResponseEntity<ApiResponse<AdminUserResponse>> changeStatus(
            @PathVariable UUID userId,
            @RequestBody ChangeStatusRequest request
    ) {

        AdminUserResponse response = adminUserService.changeUserStatus(userId, request);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("User's status changed. ",  response));

    }

}
