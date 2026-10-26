package com.server.server.module.study.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.server.server.common.exception.BizException;
import com.server.server.common.response.ApiResponse;
import com.server.server.module.study.dto.WeeklyStudyTrendVO;
import com.server.server.module.study.entity.StudyLog;
import com.server.server.module.study.service.StudyLogService;
import com.server.server.module.user.entity.SysUser;
import com.server.server.module.user.mapper.SysUserMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.security.Principal;
import java.time.temporal.TemporalAdjusters;

@RestController
@RequestMapping("/api/study-logs")
@RequiredArgsConstructor
public class StudyLogController {

    private final StudyLogService studyLogService;
    private final SysUserMapper sysUserMapper;

    @GetMapping("/page")
    public ApiResponse<Page<StudyLog>> page(
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "1") long pageNum,
            @RequestParam(defaultValue = "10") long pageSize) {
        return ApiResponse.ok(studyLogService.pageByUser(userId, pageNum, pageSize));
    }

    @PostMapping
    public ApiResponse<Boolean> save(@Valid @RequestBody StudyLog studyLog) {
        return ApiResponse.ok(studyLogService.save(studyLog));
    }

    @PutMapping
    public ApiResponse<Boolean> update(@RequestBody StudyLog studyLog) {
        return ApiResponse.ok(studyLogService.updateById(studyLog));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> remove(@PathVariable Long id) {
        return ApiResponse.ok(studyLogService.removeById(id));
    }

    @GetMapping("/weekly")
    public ApiResponse<WeeklyStudyTrendVO> weeklyTrend(
            Principal principal,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart) {
        Long realUserId = userId != null ? userId : currentUserId(principal);
        LocalDate start = weekStart == null
                ? LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                : weekStart;
        return ApiResponse.ok(studyLogService.getWeeklyTrend(realUserId, start));
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
