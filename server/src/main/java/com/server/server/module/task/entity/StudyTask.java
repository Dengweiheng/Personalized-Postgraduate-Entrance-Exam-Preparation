package com.server.server.module.task.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("study_task")
public class StudyTask {

    @TableId(type = IdType.AUTO)
    private Long id;

    @NotNull(message = "userId cannot be null")
    private Long userId;

    @NotBlank(message = "taskName cannot be blank")
    private String taskName;

    private String taskDetail;

    private LocalDateTime deadline;

    private String status;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
