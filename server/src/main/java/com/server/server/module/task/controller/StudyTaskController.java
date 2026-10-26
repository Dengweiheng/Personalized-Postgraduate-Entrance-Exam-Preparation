package com.server.server.module.task.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.server.common.exception.BizException;
import com.server.server.common.response.ApiResponse;
import com.server.server.module.task.dto.TaskStatusUpdateRequest;
import com.server.server.module.task.entity.StudyTask;
import com.server.server.module.task.service.StudyTaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class StudyTaskController {

    private final StudyTaskService studyTaskService;

    @GetMapping("/page")
    public ApiResponse<Page<StudyTask>> page(
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "1") long pageNum,
            @RequestParam(defaultValue = "10") long pageSize) {
        return ApiResponse.ok(studyTaskService.pageByUser(userId, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public ApiResponse<StudyTask> getById(@PathVariable Long id) {
        return ApiResponse.ok(studyTaskService.getById(id));
    }

    @PostMapping
    public ApiResponse<Boolean> save(@Valid @RequestBody StudyTask studyTask) {
        if (studyTask.getStatus() == null || studyTask.getStatus().isBlank()) {
            studyTask.setStatus("NOT_STARTED");
        }
        return ApiResponse.ok(studyTaskService.save(studyTask));
    }

    @PutMapping
    public ApiResponse<Boolean> update(@RequestBody StudyTask studyTask) {
        if (studyTask.getId() == null) {
            throw new BizException("task id cannot be null");
        }

        StudyTask dbTask = studyTaskService.getById(studyTask.getId());
        if (dbTask == null) {
            throw new BizException("task not found");
        }

        if (studyTask.getStatus() != null && !studyTask.getStatus().equals(dbTask.getStatus())) {
            studyTaskService.updateTaskStatus(studyTask.getId(), studyTask.getStatus());
            studyTask.setStatus(null);
        }
        return ApiResponse.ok(studyTaskService.updateById(studyTask));
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Boolean> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody TaskStatusUpdateRequest request) {
        return ApiResponse.ok(studyTaskService.updateTaskStatus(id, request.getStatus()));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> remove(@PathVariable Long id) {
        return ApiResponse.ok(studyTaskService.removeById(id));
    }
}
