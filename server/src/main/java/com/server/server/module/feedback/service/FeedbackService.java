package com.server.server.module.feedback.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.server.server.module.feedback.entity.Feedback;

public interface FeedbackService extends IService<Feedback> {

    Page<Feedback> pageByUser(Long userId, long pageNum, long pageSize);
}
