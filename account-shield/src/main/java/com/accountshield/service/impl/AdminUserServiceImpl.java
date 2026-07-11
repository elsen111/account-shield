package com.accountshield.service.impl;

import com.accountshield.dto.admin.AdminUserResponse;
import com.accountshield.dto.admin.ChangeRoleRequest;
import com.accountshield.dto.admin.ChangeStatusRequest;
import com.accountshield.entity.UserEntity;
import com.accountshield.exception.UserNotFoundException;
import com.accountshield.mapper.UserMapper;
import com.accountshield.repository.UserRepository;
import com.accountshield.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public Page<AdminUserResponse> getUsers(
            Pageable pageable
    ) {

        return userRepository.findAll(pageable).map(userMapper::toAdminResponse);

    }


    @Override
    public AdminUserResponse getUserById(UUID userId) {

        UserEntity user = findUser(userId);

        return userMapper.toAdminResponse(user);
    }

    @Override
    public AdminUserResponse changeUserStatus(UUID userId, ChangeStatusRequest request) {
        UserEntity user = findUser(userId);

        user.setStatus(request.getStatus());
        userRepository.save(user);

        return userMapper.toAdminResponse(user);

    }

    @Override
    public AdminUserResponse assignRole(UUID userId, ChangeRoleRequest request) {

        UserEntity user = findUser(userId);

        user.setRole(request.getRole());
        userRepository.save(user);

        return userMapper.toAdminResponse(user);

    }

    private UserEntity findUser(UUID id) {

        return userRepository.findById(id)
                .orElseThrow(
                        () -> new UserNotFoundException("User not found!")
                );

    }
}
