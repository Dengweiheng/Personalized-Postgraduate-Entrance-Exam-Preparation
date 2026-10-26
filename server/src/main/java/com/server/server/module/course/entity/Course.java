package com.server.server.module.course.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("course")
public class Course {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String subject;

    private String difficulty;

    private String tags;

    private String intro;

    private String coverUrl;

    private Integer status;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
