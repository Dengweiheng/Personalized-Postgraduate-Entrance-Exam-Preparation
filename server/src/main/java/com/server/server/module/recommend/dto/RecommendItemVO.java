package com.server.server.module.recommend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RecommendItemVO {

    private Long courseId;
    private String title;
    private String subject;
    private String difficulty;
    private String tags;
    private String coverUrl;
    private BigDecimal score;
    private String reason;
}
