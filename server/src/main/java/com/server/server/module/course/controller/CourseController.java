package com.server.server.module.course.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.server.common.exception.BizException;
import com.server.server.common.response.ApiResponse;
import com.server.server.module.course.dto.CourseCardVO;
import com.server.server.module.course.dto.LearnCourseRequest;
import com.server.server.module.course.entity.Course;
import com.server.server.module.course.service.CourseService;
import com.server.server.module.recommend.entity.UserCourseBehavior;
import com.server.server.module.recommend.mapper.UserCourseBehaviorMapper;
import com.server.server.module.study.entity.StudyLog;
import com.server.server.module.study.mapper.StudyLogMapper;
import com.server.server.module.user.entity.SysUser;
import com.server.server.module.user.mapper.SysUserMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final SysUserMapper sysUserMapper;
    private final StudyLogMapper studyLogMapper;
    private final UserCourseBehaviorMapper userCourseBehaviorMapper;

    @GetMapping("/categories")
    public ApiResponse<List<String>> categories() {
        return ApiResponse.ok(courseService.listCategories());
    }

    @GetMapping("/cards")
    public ApiResponse<Page<CourseCardVO>> cards(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") long pageNum,
            @RequestParam(defaultValue = "8") long pageSize) {
        return ApiResponse.ok(courseService.pageCards(category, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public ApiResponse<Course> detail(@PathVariable Long id) {
        return ApiResponse.ok(courseService.getById(id));
    }

    @PostMapping("/learn")
    public ApiResponse<Boolean> learn(@Valid @RequestBody LearnCourseRequest request, Principal principal) {
        Long userId = currentUserId(principal);

        Course course = courseService.getById(request.getCourseId());
        if (course == null || course.getStatus() == null || course.getStatus() != 1) {
            throw new BizException("course not available");
        }

        LocalDate today = LocalDate.now();
        StudyLog exist = studyLogMapper.selectOne(new LambdaQueryWrapper<StudyLog>()
                .eq(StudyLog::getUserId, userId)
                .eq(StudyLog::getLogDate, today)
                .last("limit 1"));

        if (exist == null) {
            StudyLog row = new StudyLog();
            row.setUserId(userId);
            row.setLogDate(today);
            row.setMinutesSpent(request.getMinutes());
            studyLogMapper.insert(row);
        } else {
            int current = exist.getMinutesSpent() == null ? 0 : exist.getMinutesSpent();
            exist.setMinutesSpent(current + request.getMinutes());
            studyLogMapper.updateById(exist);
        }

        UserCourseBehavior behavior = new UserCourseBehavior();
        behavior.setUserId(userId);
        behavior.setCourseId(request.getCourseId());
        behavior.setBehaviorType("LEARN");
        behavior.setScore(Math.max(1, Math.min(10, request.getMinutes() / 15)));
        userCourseBehaviorMapper.insert(behavior);

        return ApiResponse.ok(true);
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
