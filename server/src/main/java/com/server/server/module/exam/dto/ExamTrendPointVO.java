package com.server.server.module.exam.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ExamTrendPointVO {

    private String examName;
    private LocalDate examDate;
    private BigDecimal politicsScore;
    private BigDecimal englishScore;
    private BigDecimal mathScore;
    private BigDecimal majorScore;
    private BigDecimal totalScore;
}
