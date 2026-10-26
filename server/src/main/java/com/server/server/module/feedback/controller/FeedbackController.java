package com.server.server.module.feedback.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.server.common.response.ApiResponse;
import com.server.server.module.feedback.entity.Feedback;
import com.server.server.module.feedback.service.FeedbackService;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @GetMapping("/page")
    public ApiResponse<Page<Feedback>> page(
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "1") long pageNum,
            @RequestParam(defaultValue = "10") long pageSize) {
        return ApiResponse.ok(feedbackService.pageByUser(userId, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public ApiResponse<Feedback> getById(@PathVariable Long id) {
        return ApiResponse.ok(feedbackService.getById(id));
    }

    @PostMapping
    public ApiResponse<Boolean> save(@Valid @RequestBody Feedback feedback) {
        if (feedback.getStatus() == null || feedback.getStatus().isBlank()) {
            feedback.setStatus("PENDING");
        }
        return ApiResponse.ok(feedbackService.save(feedback));
    }

    @PutMapping
    public ApiResponse<Boolean> update(@RequestBody Feedback feedback) {
        return ApiResponse.ok(feedbackService.updateById(feedback));
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Boolean> updateStatus(@PathVariable Long id, @RequestBody StatusRequest request) {
        Feedback row = feedbackService.getById(id);
        if (row == null) {
            return ApiResponse.ok(false);
        }
        row.setStatus(request.getStatus());
        return ApiResponse.ok(feedbackService.updateById(row));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> remove(@PathVariable Long id) {
        return ApiResponse.ok(feedbackService.removeById(id));
    }

    @PostMapping("/upload")
    public ApiResponse<List<String>> upload(@RequestParam("files") MultipartFile[] files) throws IOException {
        Path dir = Paths.get(System.getProperty("user.dir"), "uploads", "feedback").toAbsolutePath().normalize();
        Files.createDirectories(dir);

        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                continue;
            }
            String ext = "";
            String filename = file.getOriginalFilename();
            if (filename != null && filename.contains(".")) {
                ext = filename.substring(filename.lastIndexOf('.'));
            }
            String storedName = UUID.randomUUID() + ext;
            Path target = dir.resolve(storedName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            urls.add("/uploads/feedback/" + storedName);
        }
        return ApiResponse.ok(urls);
    }

    @Data
    public static class StatusRequest {
        private String status;
    }

    @PutMapping("/{id}/reply")
    public ApiResponse<Boolean> handleReply(@PathVariable Long id, @RequestBody ReplyRequest request) {
        Feedback row = feedbackService.getById(id);
        if (row == null) {
            return ApiResponse.ok(false);
        }
        row.setAdminReply(request.getReply());
        row.setStatus("RESOLVED");
        row.setRepliedAt(java.time.LocalDateTime.now());
        return ApiResponse.ok(feedbackService.updateById(row));
    }

    @Data
    public static class ReplyRequest {
        private String reply;
    }
}
