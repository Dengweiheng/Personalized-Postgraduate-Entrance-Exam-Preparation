package com.server.server.module.feedback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.server.server.module.feedback.entity.Feedback;
import com.server.server.module.feedback.mapper.FeedbackMapper;
import com.server.server.module.feedback.service.FeedbackService;
import org.springframework.stereotype.Service;

@Service
public class FeedbackServiceImpl extends ServiceImpl<FeedbackMapper, Feedback> implements FeedbackService {

    @Override
    public Page<Feedback> pageByUser(Long userId, long pageNum, long pageSize) {
        LambdaQueryWrapper<Feedback> wrapper = new LambdaQueryWrapper<Feedback>()
                .eq(userId != null, Feedback::getUserId, userId)
                .orderByDesc(Feedback::getCreatedAt);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }
}
