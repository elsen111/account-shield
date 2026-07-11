package com.accountshield.dto.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Password change request")
public class ChangePasswordRequest {

    @NotBlank(message = "Current password is required")
    @Schema(example = "Password123!")
    private String oldPassword;

    @NotBlank(message = "New password is required.")
    @Size(min = 8, message = "Password must contain at least 8 characters")
    @Schema(example = "Pass123!")
    private String newPassword;

}
