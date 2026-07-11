package com.accountshield.dto.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Profile update request")
public class UpdateProfileRequest {

    @NotBlank(message = "Username is required.")
    @Size(max = 50)
    @Schema(example = "john125")
    private String username;

}
