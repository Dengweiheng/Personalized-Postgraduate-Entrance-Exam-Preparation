package com.server.server.module.study.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.server.server.module.study.dto.WeeklyStudyTrendVO;
import com.server.server.module.study.entity.StudyLog;

import java.time.LocalDate;

public interface StudyLogService extends IService<StudyLog> {

    Page<StudyLog> pageByUser(Long userId, long pageNum, long pageSize);

    WeeklyStudyTrendVO getWeeklyTrend(Long userId, LocalDate weekStart);
}
