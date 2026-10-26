package com.server.server.module.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.server.common.exception.BizException;
import com.server.server.common.response.ApiResponse;
import com.server.server.module.admin.dto.StudentDataOverviewVO;
import com.server.server.module.exam.entity.ExamScore;
import com.server.server.module.exam.mapper.ExamScoreMapper;
import com.server.server.module.feedback.entity.Feedback;
import com.server.server.module.feedback.mapper.FeedbackMapper;
import com.server.server.module.study.entity.StudyLog;
import com.server.server.module.study.mapper.StudyLogMapper;
import com.server.server.module.task.entity.StudyTask;
import com.server.server.module.task.mapper.StudyTaskMapper;
import com.server.server.module.user.entity.SysUser;
import com.server.server.module.user.mapper.SysUserMapper;
import com.server.server.module.wrong.entity.WrongQuestion;
import com.server.server.module.wrong.mapper.WrongQuestionMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/students")
@RequiredArgsConstructor
public class AdminStudentController {

    private final SysUserMapper sysUserMapper;
    private final StudyTaskMapper studyTaskMapper;
    private final StudyLogMapper studyLogMapper;
    private final ExamScoreMapper examScoreMapper;
    private final WrongQuestionMapper wrongQuestionMapper;
    private final FeedbackMapper feedbackMapper;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/page")
    public ApiResponse<Page<SysUser>> page(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") long pageNum,
            @RequestParam(defaultValue = "10") long pageSize) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .like(keyword != null && !keyword.isBlank(), SysUser::getUsername, keyword)
                .or(keyword != null && !keyword.isBlank())
                .like(keyword != null && !keyword.isBlank(), SysUser::getNickname, keyword)
                .orderByDesc(SysUser::getId);
        Page<SysUser> page = sysUserMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return ApiResponse.ok(page);
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Boolean> updateStatus(@PathVariable Long id, @RequestBody StatusRequest request) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BizException("user not found");
        }
        user.setStatus(request.getStatus());
        return ApiResponse.ok(sysUserMapper.updateById(user) > 0);
    }

    @PutMapping("/{id}/reset-password")
    public ApiResponse<Boolean> resetPassword(@PathVariable Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BizException("user not found");
        }
        user.setPassword(passwordEncoder.encode("123456"));
        return ApiResponse.ok(sysUserMapper.updateById(user) > 0);
    }

    @GetMapping("/{id}/overview")
    public ApiResponse<StudentDataOverviewVO> overview(@PathVariable Long id) {
        StudentDataOverviewVO vo = new StudentDataOverviewVO();
        vo.setUserId(id);
        vo.setTaskCount(studyTaskMapper.selectCount(new LambdaQueryWrapper<StudyTask>().eq(StudyTask::getUserId, id)));
        vo.setStudyLogCount(studyLogMapper.selectCount(new LambdaQueryWrapper<StudyLog>().eq(StudyLog::getUserId, id)));
        vo.setExamCount(examScoreMapper.selectCount(new LambdaQueryWrapper<ExamScore>().eq(ExamScore::getUserId, id)));
        vo.setWrongQuestionCount(wrongQuestionMapper.selectCount(new LambdaQueryWrapper<WrongQuestion>().eq(WrongQuestion::getUserId, id)));
        vo.setFeedbackCount(feedbackMapper.selectCount(new LambdaQueryWrapper<Feedback>().eq(Feedback::getUserId, id)));
        return ApiResponse.ok(vo);
    }

    @Data
    public static class StatusRequest {
        private Integer status;
    }
}
