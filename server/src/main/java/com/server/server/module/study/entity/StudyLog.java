package com.server.server.module.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("study_log")
public class StudyLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    @NotNull(message = "userId cannot be null")
    private Long userId;

    @NotNull(message = "logDate cannot be null")
    private LocalDate logDate;

    @NotNull(message = "minutesSpent cannot be null")
    private Integer minutesSpent;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
