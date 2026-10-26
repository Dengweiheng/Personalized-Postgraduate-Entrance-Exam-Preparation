package com.server.server.module.wrong.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.server.common.response.ApiResponse;
import com.server.server.module.wrong.entity.WrongQuestion;
import com.server.server.module.wrong.service.WrongQuestionService;
import jakarta.validation.Valid;
import lombok.Data;
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
@RequestMapping("/api/wrong-questions")
@RequiredArgsConstructor
public class WrongQuestionController {

    private final WrongQuestionService wrongQuestionService;

    @GetMapping("/page")
    public ApiResponse<Page<WrongQuestion>> page(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String masteryStatus,
            @RequestParam(defaultValue = "1") long pageNum,
            @RequestParam(defaultValue = "10") long pageSize) {
        return ApiResponse.ok(wrongQuestionService.pageByUser(userId, masteryStatus, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public ApiResponse<WrongQuestion> getById(@PathVariable Long id) {
        return ApiResponse.ok(wrongQuestionService.getById(id));
    }

    @PostMapping
    public ApiResponse<Boolean> save(@Valid @RequestBody WrongQuestion wrongQuestion) {
        if (wrongQuestion.getMasteryStatus() == null || wrongQuestion.getMasteryStatus().isBlank()) {
            wrongQuestion.setMasteryStatus("UNMASTERED");
        }
        return ApiResponse.ok(wrongQuestionService.save(wrongQuestion));
    }

    @PutMapping
    public ApiResponse<Boolean> update(@RequestBody WrongQuestion wrongQuestion) {
        return ApiResponse.ok(wrongQuestionService.updateById(wrongQuestion));
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Boolean> updateStatus(@PathVariable Long id, @RequestBody StatusRequest request) {
        return ApiResponse.ok(wrongQuestionService.updateMasteryStatus(id, request.getMasteryStatus()));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> remove(@PathVariable Long id) {
        return ApiResponse.ok(wrongQuestionService.removeById(id));
    }

    @Data
    public static class StatusRequest {
        private String masteryStatus;
    }
}
