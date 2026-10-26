package com.server.server.module.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("exam_score")
public class ExamScore {

    @TableId(type = IdType.AUTO)
    private Long id;

    @NotNull(message = "userId cannot be null")
    private Long userId;

    @NotBlank(message = "examName cannot be blank")
    private String examName;

    @NotNull(message = "examDate cannot be null")
    private LocalDate examDate;

    private BigDecimal politicsScore;
    private BigDecimal englishScore;
    private BigDecimal mathScore;
    private BigDecimal majorScore;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
