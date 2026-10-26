package com.server.server.module.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.server.server.common.response.ApiResponse;
import com.server.server.module.recommend.entity.RecommendationResult;
import com.server.server.module.recommend.mapper.RecommendationResultMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/system")
@RequiredArgsConstructor
public class AdminSystemController {

    private final RecommendationResultMapper recommendationResultMapper;

    @GetMapping("/health")
    public ApiResponse<Map<String, Object>> health() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("time", LocalDateTime.now());
        return ApiResponse.ok(data);
    }

    @DeleteMapping("/recommendation/cleanup")
    public ApiResponse<Integer> cleanupRecommendation(@RequestParam(defaultValue = "30") int days) {
        LocalDateTime threshold = LocalDateTime.now().minusDays(days);
        int deleted = recommendationResultMapper.delete(new LambdaQueryWrapper<RecommendationResult>()
                .lt(RecommendationResult::getCreatedAt, threshold));
        return ApiResponse.ok(deleted);
    }
}
