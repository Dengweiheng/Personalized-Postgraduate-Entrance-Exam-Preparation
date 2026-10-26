package com.server.server.module.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginUserVO {

    private Long userId;
    private String username;
    private String nickname;
    private String role;
}
