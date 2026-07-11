package com.accountshield.dto.admin;

import com.accountshield.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Role assignment request")
public class ChangeRoleRequest {

    @NotNull(message = "Role is required")
    @Schema(example = "ADMIN / USER")
    private Role role;

}
