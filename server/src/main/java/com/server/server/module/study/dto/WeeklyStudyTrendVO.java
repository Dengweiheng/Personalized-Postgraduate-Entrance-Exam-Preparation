package com.server.server.module.study.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class WeeklyStudyTrendVO {

    private LocalDate weekStart;
    private LocalDate weekEnd;
    private Integer totalMinutes;
    private List<StudyTrendPointVO> points;
}
