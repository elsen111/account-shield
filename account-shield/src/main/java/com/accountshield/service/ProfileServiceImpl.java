package com.accountshield.service;

import com.accountshield.dto.common.UserResponse;
import com.accountshield.dto.profile.ChangePasswordRequest;
import com.accountshield.dto.profile.UpdateProfileRequest;
import com.accountshield.entity.UserEntity;
import com.accountshield.exception.BadRequestException;
import com.accountshield.exception.UserNotFoundException;
import com.accountshield.mapper.UserMapper;
import com.accountshield.repository.UserRepository;
import com.accountshield.security.jwt.JwtService;
import com.accountshield.security.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse getProfile() {

        return userMapper.toResponse(getCurrentUser());

    }

    @Override
    public UserResponse updateProfile(UpdateProfileRequest request) {

        UserEntity user = getCurrentUser();

        user.setUsername(request.getUsername());

        userRepository.save(user);

        return userMapper.toResponse(user);

    }

    @Override
    public void changePassword(
            ChangePasswordRequest request
    ) {

        UserEntity user = getCurrentUser();

        if (!passwordEncoder.matches(
                request.getOldPassword(),
                user.getPassword()
        )) {

            throw new BadRequestException("Old password is incorrect.");

        }

        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        userRepository.save(user);

    }

    private UserEntity getCurrentUser() {

        return userRepository.findById(
                SecurityUtils.getCurrentUserId()
        ).orElseThrow(
                () -> new UserNotFoundException("User not found.")
        );

    }
}
