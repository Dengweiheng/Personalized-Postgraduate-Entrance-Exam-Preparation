package com.server.server.module.task.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.server.server.module.task.entity.StudyTask;

public interface StudyTaskService extends IService<StudyTask> {

    Page<StudyTask> pageByUser(Long userId, long pageNum, long pageSize);

    boolean updateTaskStatus(Long id, String newStatus);
}
