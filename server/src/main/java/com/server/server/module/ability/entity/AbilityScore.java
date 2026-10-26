package com.server.server.module.ability.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("ability_score")
public class AbilityScore {

    @TableId(type = IdType.AUTO)
    private Long id;

    @NotNull(message = "userId cannot be null")
    private Long userId;

    @NotNull(message = "statDate cannot be null")
    private LocalDate statDate;

    private BigDecimal learningIntensity;
    private BigDecimal knowledgeConsolidation;
    private BigDecimal timeManagement;
    private BigDecimal persistence;
    private BigDecimal thinkingExpansion;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
