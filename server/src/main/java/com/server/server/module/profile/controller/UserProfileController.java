package com.server.server.module.profile.controller;

import com.server.server.common.response.ApiResponse;
import com.server.server.module.profile.entity.UserProfile;
import com.server.server.module.profile.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping
    public ApiResponse<UserProfile> getByUserId(@RequestParam Long userId) {
        return ApiResponse.ok(userProfileService.getByUserId(userId));
    }

    @PostMapping
    public ApiResponse<Boolean> saveOrUpdate(@Valid @RequestBody UserProfile profile) {
        return ApiResponse.ok(userProfileService.saveOrUpdateByUserId(profile));
    }
}
