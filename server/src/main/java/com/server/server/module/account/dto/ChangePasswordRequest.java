package com.server.server.module.account.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @NotBlank(message = "oldPassword cannot be blank")
    private String oldPassword;

    @NotBlank(message = "newPassword cannot be blank")
    private String newPassword;
}
