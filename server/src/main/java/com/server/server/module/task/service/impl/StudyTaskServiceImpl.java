package com.server.server.module.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.server.server.common.exception.BizException;
import com.server.server.module.task.entity.StudyTask;
import com.server.server.module.task.mapper.StudyTaskMapper;
import com.server.server.module.task.service.StudyTaskService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class StudyTaskServiceImpl extends ServiceImpl<StudyTaskMapper, StudyTask> implements StudyTaskService {

    private static final Map<String, Set<String>> STATUS_FLOW = Map.of(
            "NOT_STARTED", Set.of("IN_PROGRESS"),
            "IN_PROGRESS", Set.of("DONE"),
            "DONE", Set.of()
    );

    @Override
    public Page<StudyTask> pageByUser(Long userId, long pageNum, long pageSize) {
        LambdaQueryWrapper<StudyTask> wrapper = new LambdaQueryWrapper<StudyTask>()
                .eq(userId != null, StudyTask::getUserId, userId)
                .orderByDesc(StudyTask::getCreatedAt);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public boolean updateTaskStatus(Long id, String newStatus) {
        StudyTask task = this.getById(id);
        if (task == null) {
            throw new BizException("task not found");
        }

        String currentStatus = task.getStatus();
        if (currentStatus == null || currentStatus.isBlank()) {
            currentStatus = "NOT_STARTED";
        }

        if (!STATUS_FLOW.containsKey(newStatus)) {
            throw new BizException("invalid task status");
        }

        if (!STATUS_FLOW.containsKey(currentStatus)) {
            throw new BizException("invalid current status: " + currentStatus);
        }

        if (!STATUS_FLOW.get(currentStatus).contains(newStatus)) {
            throw new BizException("invalid status flow: " + currentStatus + " -> " + newStatus);
        }

        task.setStatus(newStatus);
        return this.updateById(task);
    }
}
