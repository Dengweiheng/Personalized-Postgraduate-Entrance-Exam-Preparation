package com.server.server.module.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.server.server.common.response.ApiResponse;
import com.server.server.module.admin.dto.AdminDashboardStatsVO;
import com.server.server.module.admin.dto.DailyMinutesStatVO;
import com.server.server.module.course.entity.Course;
import com.server.server.module.course.mapper.CourseMapper;
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
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
public class AdminStatsController {

    private final SysUserMapper sysUserMapper;
    private final CourseMapper courseMapper;
    private final StudyTaskMapper studyTaskMapper;
    private final ExamScoreMapper examScoreMapper;
    private final WrongQuestionMapper wrongQuestionMapper;
    private final FeedbackMapper feedbackMapper;
    private final StudyLogMapper studyLogMapper;

    @GetMapping("/dashboard")
    public ApiResponse<AdminDashboardStatsVO> dashboard() {
        AdminDashboardStatsVO vo = new AdminDashboardStatsVO();
        vo.setTotalUsers(sysUserMapper.selectCount(new LambdaQueryWrapper<>()));
        vo.setActiveUsers(sysUserMapper.selectCount(new LambdaQueryWrapper<SysUser>().eq(SysUser::getStatus, 1)));
        vo.setTotalCourses(courseMapper.selectCount(new LambdaQueryWrapper<Course>()));
        vo.setTotalTasks(studyTaskMapper.selectCount(new LambdaQueryWrapper<StudyTask>()));
        vo.setTotalExams(examScoreMapper.selectCount(new LambdaQueryWrapper<ExamScore>()));
        vo.setTotalWrongQuestions(wrongQuestionMapper.selectCount(new LambdaQueryWrapper<WrongQuestion>()));
        vo.setTotalFeedback(feedbackMapper.selectCount(new LambdaQueryWrapper<Feedback>()));
        vo.setPendingFeedback(feedbackMapper.selectCount(new LambdaQueryWrapper<Feedback>().eq(Feedback::getStatus, "PENDING")));

        LocalDate start = LocalDate.now().minusDays(6);
        LocalDate end = LocalDate.now();
        List<StudyLog> logs = studyLogMapper.selectList(new LambdaQueryWrapper<StudyLog>()
                .between(StudyLog::getLogDate, start, end));

        Map<LocalDate, Integer> dayMinutes = new HashMap<>();
        int total = 0;
        for (StudyLog log : logs) {
            int m = log.getMinutesSpent() == null ? 0 : log.getMinutesSpent();
            total += m;
            dayMinutes.put(log.getLogDate(), dayMinutes.getOrDefault(log.getLogDate(), 0) + m);
        }

        List<DailyMinutesStatVO> trend = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate d = start.plusDays(i);
            trend.add(new DailyMinutesStatVO(d, dayMinutes.getOrDefault(d, 0)));
        }

        vo.setWeekTotalMinutes(total);
        vo.setWeekMinutesTrend(trend);

        // Course distributions
        List<Course> allCourses = courseMapper.selectList(null);
        Map<String, Long> subjectMap = new HashMap<>();
        Map<String, Long> difficultyMap = new HashMap<>();
        for (Course c : allCourses) {
            if (c.getSubject() != null) {
                subjectMap.put(c.getSubject(), subjectMap.getOrDefault(c.getSubject(), 0L) + 1);
            }
            if (c.getDifficulty() != null) {
                difficultyMap.put(c.getDifficulty(), difficultyMap.getOrDefault(c.getDifficulty(), 0L) + 1);
            }
        }
        vo.setSubjectStats(subjectMap);
        vo.setDifficultyStats(difficultyMap);

        return ApiResponse.ok(vo);
    }
}
