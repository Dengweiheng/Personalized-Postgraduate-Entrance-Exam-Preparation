package com.server.server.module.feedback.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("feedback")
public class Feedback {

    @TableId(type = IdType.AUTO)
    private Long id;

    @NotNull(message = "userId cannot be null")
    private Long userId;

    @NotBlank(message = "feedbackType cannot be blank")
    private String feedbackType;

    @NotBlank(message = "title cannot be blank")
    private String title;

    @NotBlank(message = "content cannot be blank")
    private String content;

    private String contactInfo;

    @TableField("attachment_urls")
    private String attachmentUrls;

    private String status;
    private String adminReply;
    @TableField("replied_at")
    private LocalDateTime repliedAt;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
