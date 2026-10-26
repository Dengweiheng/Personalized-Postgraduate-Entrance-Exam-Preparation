package com.server.server.module.exam.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.server.common.response.ApiResponse;
import com.server.server.module.exam.dto.ExamTrendPointVO;
import com.server.server.module.exam.entity.ExamScore;
import com.server.server.module.exam.service.ExamScoreService;
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

import java.util.List;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamScoreController {

    private final ExamScoreService examScoreService;

    @GetMapping("/page")
    public ApiResponse<Page<ExamScore>> page(
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "1") long pageNum,
            @RequestParam(defaultValue = "10") long pageSize) {
        return ApiResponse.ok(examScoreService.pageByUser(userId, pageNum, pageSize));
    }

    @GetMapping("/trend")
    public ApiResponse<List<ExamTrendPointVO>> trend(@RequestParam Long userId) {
        return ApiResponse.ok(examScoreService.trend(userId));
    }

    @PostMapping
    public ApiResponse<Boolean> save(@Valid @RequestBody ExamScore examScore) {
        return ApiResponse.ok(examScoreService.save(examScore));
    }

    @PutMapping
    public ApiResponse<Boolean> update(@RequestBody ExamScore examScore) {
        return ApiResponse.ok(examScoreService.updateById(examScore));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> remove(@PathVariable Long id) {
        return ApiResponse.ok(examScoreService.removeById(id));
    }
}
