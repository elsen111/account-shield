package com.accountshield.mapper;

import com.accountshield.dto.admin.AdminUserResponse;
import com.accountshield.dto.auth.RegisterRequest;
import com.accountshield.dto.common.UserResponse;
import com.accountshield.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity toEntity(RegisterRequest request);

    UserResponse toResponse(UserEntity user);

    AdminUserResponse toAdminResponse(UserEntity user);

}
