package com.server.server.module.course.dto;

import lombok.Data;

@Data
public class CourseCardVO {

    private Long id;
    private String title;
    private String subject;
    private String difficulty;
    private String tags;
    private String coverUrl;
}
