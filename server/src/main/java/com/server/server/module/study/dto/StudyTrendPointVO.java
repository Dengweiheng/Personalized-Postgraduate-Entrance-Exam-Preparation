package com.server.server.module.study.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class StudyTrendPointVO {

    private LocalDate date;
    private Integer minutes;
}
