package com.server.server.module.recommend.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user_profile")
public class UserProfileLite {

    @TableId
    private Long userId;

    private String targetSchool;

    private String targetMajor;

    private String interestTags;

    private String weakSubjects;
}
