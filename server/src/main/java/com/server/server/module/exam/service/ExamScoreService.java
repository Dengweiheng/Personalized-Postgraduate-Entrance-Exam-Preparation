package com.server.server.module.exam.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.server.server.module.exam.dto.ExamTrendPointVO;
import com.server.server.module.exam.entity.ExamScore;

import java.util.List;

public interface ExamScoreService extends IService<ExamScore> {

    Page<ExamScore> pageByUser(Long userId, long pageNum, long pageSize);

    List<ExamTrendPointVO> trend(Long userId);
}
