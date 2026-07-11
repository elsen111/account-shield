package com.accountshield.service;

import com.accountshield.dto.admin.AdminUserResponse;
import com.accountshield.dto.admin.ChangeRoleRequest;
import com.accountshield.dto.admin.ChangeStatusRequest;
import com.accountshield.enums.Role;
import com.accountshield.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AdminUserService {

    Page<AdminUserResponse> getUsers(Pageable pageable);

    AdminUserResponse getUserById(UUID id);

    AdminUserResponse changeUserStatus(UUID userId, ChangeStatusRequest request);

    AdminUserResponse assignRole(UUID userId, ChangeRoleRequest request);

}
