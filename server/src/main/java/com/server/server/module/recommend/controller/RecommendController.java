package com.server.server.module.recommend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.server.server.common.exception.BizException;
import com.server.server.common.response.ApiResponse;
import com.server.server.module.recommend.dto.RecommendItemVO;
import com.server.server.module.recommend.entity.UserCourseBehavior;
import com.server.server.module.user.entity.SysUser;
import com.server.server.module.user.mapper.SysUserMapper;
import com.server.server.module.recommend.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;
    private final SysUserMapper sysUserMapper;

    @PostMapping("/behavior")
    public ApiResponse<Boolean> addBehavior(@RequestBody UserCourseBehavior behavior, Principal principal) {
        if (behavior.getCourseId() == null) {
            throw new BizException("courseId cannot be null");
        }
        if (behavior.getBehaviorType() == null || behavior.getBehaviorType().isBlank()) {
            behavior.setBehaviorType("VIEW");
        }
        if (behavior.getScore() == null) {
            behavior.setScore(1);
        }
        behavior.setUserId(currentUserId(principal));
        return ApiResponse.ok(recommendService.addBehavior(behavior));
    }

    @GetMapping("/cf")
    public ApiResponse<List<RecommendItemVO>> cf(
            Principal principal,
            @RequestParam(defaultValue = "8") int limit) {
        return ApiResponse.ok(recommendService.cfRecommend(currentUserId(principal), limit));
    }

    @GetMapping("/content")
    public ApiResponse<List<RecommendItemVO>> content(
            Principal principal,
            @RequestParam(defaultValue = "8") int limit) {
        return ApiResponse.ok(recommendService.contentRecommend(currentUserId(principal), limit));
    }

    private Long currentUserId(Principal principal) {
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, principal.getName())
                .last("limit 1"));
        if (user == null) {
            throw new BizException("user not found");
        }
        return user.getId();
    }
}
