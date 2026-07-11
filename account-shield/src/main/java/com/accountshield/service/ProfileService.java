package com.accountshield.service;

import com.accountshield.dto.common.UserResponse;
import com.accountshield.dto.profile.ChangePasswordRequest;
import com.accountshield.dto.profile.UpdateProfileRequest;

public interface ProfileService {

    UserResponse getProfile();

    UserResponse updateProfile(
            UpdateProfileRequest request
    );

    void changePassword(
            ChangePasswordRequest request
    );

}
