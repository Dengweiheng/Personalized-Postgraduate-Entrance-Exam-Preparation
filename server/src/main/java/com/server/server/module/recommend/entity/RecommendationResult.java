package com.server.server.module.recommend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("recommendation_result")
public class RecommendationResult {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String algorithmType;

    private Long courseId;

    private BigDecimal score;

    private String reasonText;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
