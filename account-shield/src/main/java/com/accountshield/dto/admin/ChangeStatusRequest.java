package com.accountshield.dto.admin;

import com.accountshield.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Status modification request")
public class ChangeStatusRequest {

    @NotNull(message = "Status is required")
    @Schema(example = "INACTIVE / ACTIVE / SUSPENDED")
    private UserStatus status;

}
