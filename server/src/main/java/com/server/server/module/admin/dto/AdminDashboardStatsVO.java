package com.server.server.module.admin.dto;

import lombok.Data;

import java.util.List;

@Data
public class AdminDashboardStatsVO {

    private Long totalUsers;
    private Long activeUsers;
    private Long totalCourses;
    private Long totalTasks;
    private Long totalExams;
    private Long totalWrongQuestions;
    private Long totalFeedback;
    private Long pendingFeedback;
    private Integer weekTotalMinutes;
    private List<DailyMinutesStatVO> weekMinutesTrend;
    private Object subjectStats; // Map<String, Long>
    private Object difficultyStats; // Map<String, Long>
}
