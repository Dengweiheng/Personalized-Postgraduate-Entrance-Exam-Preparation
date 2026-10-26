package com.server.server.module.course.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LearnCourseRequest {

    @NotNull(message = "courseId cannot be null")
    private Long courseId;

    @NotNull(message = "minutes cannot be null")
    @Min(value = 1, message = "minutes must be >= 1")
    @Max(value = 600, message = "minutes must be <= 600")
    private Integer minutes;
}
