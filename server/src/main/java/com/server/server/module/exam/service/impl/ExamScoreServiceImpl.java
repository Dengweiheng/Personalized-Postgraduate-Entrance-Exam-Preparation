package com.server.server.module.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.server.server.module.exam.dto.ExamTrendPointVO;
import com.server.server.module.exam.entity.ExamScore;
import com.server.server.module.exam.mapper.ExamScoreMapper;
import com.server.server.module.exam.service.ExamScoreService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExamScoreServiceImpl extends ServiceImpl<ExamScoreMapper, ExamScore> implements ExamScoreService {

    @Override
    public Page<ExamScore> pageByUser(Long userId, long pageNum, long pageSize) {
        LambdaQueryWrapper<ExamScore> wrapper = new LambdaQueryWrapper<ExamScore>()
                .eq(userId != null, ExamScore::getUserId, userId)
                .orderByDesc(ExamScore::getExamDate);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public List<ExamTrendPointVO> trend(Long userId) {
        List<ExamScore> list = this.list(new LambdaQueryWrapper<ExamScore>()
                .eq(ExamScore::getUserId, userId)
                .orderByAsc(ExamScore::getExamDate));

        List<ExamTrendPointVO> result = new ArrayList<>();
        for (ExamScore exam : list) {
            ExamTrendPointVO vo = new ExamTrendPointVO();
            vo.setExamName(exam.getExamName());
            vo.setExamDate(exam.getExamDate());
            vo.setPoliticsScore(exam.getPoliticsScore());
            vo.setEnglishScore(exam.getEnglishScore());
            vo.setMathScore(exam.getMathScore());
            vo.setMajorScore(exam.getMajorScore());

            BigDecimal total = safe(exam.getPoliticsScore())
                    .add(safe(exam.getEnglishScore()))
                    .add(safe(exam.getMathScore()))
                    .add(safe(exam.getMajorScore()));
            vo.setTotalScore(total);

            result.add(vo);
        }
        return result;
    }

    private BigDecimal safe(BigDecimal val) {
        return val == null ? BigDecimal.ZERO : val;
    }
}
