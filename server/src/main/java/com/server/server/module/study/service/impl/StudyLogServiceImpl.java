package com.server.server.module.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.server.server.module.study.dto.StudyTrendPointVO;
import com.server.server.module.study.dto.WeeklyStudyTrendVO;
import com.server.server.module.study.entity.StudyLog;
import com.server.server.module.study.mapper.StudyLogMapper;
import com.server.server.module.study.service.StudyLogService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StudyLogServiceImpl extends ServiceImpl<StudyLogMapper, StudyLog> implements StudyLogService {

    @Override
    public Page<StudyLog> pageByUser(Long userId, long pageNum, long pageSize) {
        LambdaQueryWrapper<StudyLog> wrapper = new LambdaQueryWrapper<StudyLog>()
                .eq(userId != null, StudyLog::getUserId, userId)
                .orderByDesc(StudyLog::getLogDate);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public WeeklyStudyTrendVO getWeeklyTrend(Long userId, LocalDate weekStart) {
        LocalDate end = weekStart.plusDays(6);

        List<StudyLog> logs = this.list(new LambdaQueryWrapper<StudyLog>()
                .eq(StudyLog::getUserId, userId)
                .between(StudyLog::getLogDate, weekStart, end)
                .orderByAsc(StudyLog::getLogDate));

        Map<LocalDate, Integer> map = new HashMap<>();
        for (StudyLog log : logs) {
            map.put(log.getLogDate(), log.getMinutesSpent());
        }

        List<StudyTrendPointVO> points = new ArrayList<>();
        int total = 0;
        for (int i = 0; i < 7; i++) {
            LocalDate date = weekStart.plusDays(i);
            int minutes = map.getOrDefault(date, 0);
            total += minutes;
            points.add(new StudyTrendPointVO(date, minutes));
        }

        WeeklyStudyTrendVO vo = new WeeklyStudyTrendVO();
        vo.setWeekStart(weekStart);
        vo.setWeekEnd(end);
        vo.setTotalMinutes(total);
        vo.setPoints(points);
        return vo;
    }
}
