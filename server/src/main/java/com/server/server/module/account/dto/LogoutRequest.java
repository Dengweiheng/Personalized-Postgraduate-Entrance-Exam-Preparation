package com.server.server.module.account.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LogoutRequest {

    @NotNull(message = "userId cannot be null")
    private Long userId;
}
