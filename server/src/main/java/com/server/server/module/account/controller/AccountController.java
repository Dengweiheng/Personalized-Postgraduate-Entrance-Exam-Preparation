package com.server.server.module.account.controller;

import com.server.server.common.response.ApiResponse;
import com.server.server.module.account.dto.ChangePasswordRequest;
import com.server.server.module.account.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/change-password")
    public ApiResponse<Boolean> changePassword(@Valid @RequestBody ChangePasswordRequest request, Principal principal) {
        return ApiResponse.ok(accountService.changePassword(
                principal.getName(), request.getOldPassword(), request.getNewPassword()));
    }

    @PostMapping("/logout")
    public ApiResponse<Boolean> logout(Principal principal) {
        return ApiResponse.ok(accountService.logout(principal.getName()));
    }
}
