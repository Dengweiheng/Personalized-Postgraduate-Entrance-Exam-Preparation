package com.server.server.module.admin.dto;

import lombok.Data;

@Data
public class StudentDataOverviewVO {

    private Long userId;
    private Long taskCount;
    private Long studyLogCount;
    private Long examCount;
    private Long wrongQuestionCount;
    private Long feedbackCount;
}
