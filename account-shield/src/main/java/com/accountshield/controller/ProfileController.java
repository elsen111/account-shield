package com.accountshield.controller;

import com.accountshield.common.ApiResponse;
import com.accountshield.dto.common.UserResponse;
import com.accountshield.dto.profile.ChangePasswordRequest;
import com.accountshield.dto.profile.UpdateProfileRequest;
import com.accountshield.entity.UserEntity;
import com.accountshield.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Profile",
        description = "Manage authenticated user's profile"
)
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @Operation(summary = "Get current profile")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile(){

        UserResponse response = profileService.getProfile();

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Profile data fetched successfully.", response));

    }

    @Operation(summary = "Update current profile")
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateMyProfile(
            @Valid @RequestBody UpdateProfileRequest request
            ) {
        UserResponse response = profileService.updateProfile(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Profile data updated successfully.", response));
    }

    @Operation(summary = "Update profile password")
    @PutMapping("/me/password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request
    ) {

        profileService.changePassword(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Password changed.", null));

    }

}
