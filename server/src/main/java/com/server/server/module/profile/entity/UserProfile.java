package com.server.server.module.profile.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_profile")
public class UserProfile {

    @TableId
    @NotNull(message = "userId cannot be null")
    private Long userId;

    private String targetSchool;

    private String targetMajor;

    private String interestTags;

    private String weakSubjects;

    private LocalDateTime updatedAt;
}
