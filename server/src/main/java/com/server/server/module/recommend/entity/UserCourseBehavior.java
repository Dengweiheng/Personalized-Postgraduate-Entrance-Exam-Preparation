package com.server.server.module.recommend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_course_behavior")
public class UserCourseBehavior {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    @NotNull(message = "courseId cannot be null")
    private Long courseId;

    private String behaviorType;

    private Integer score;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
