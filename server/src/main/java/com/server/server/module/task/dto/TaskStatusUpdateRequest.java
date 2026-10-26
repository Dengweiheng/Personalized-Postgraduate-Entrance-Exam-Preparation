package com.server.server.module.task.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TaskStatusUpdateRequest {

    @NotBlank(message = "status cannot be blank")
    private String status;
}
