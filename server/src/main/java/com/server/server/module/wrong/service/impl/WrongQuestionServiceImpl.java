package com.server.server.module.wrong.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.server.server.module.wrong.entity.WrongQuestion;
import com.server.server.module.wrong.mapper.WrongQuestionMapper;
import com.server.server.module.wrong.service.WrongQuestionService;
import org.springframework.stereotype.Service;

@Service
public class WrongQuestionServiceImpl extends ServiceImpl<WrongQuestionMapper, WrongQuestion> implements WrongQuestionService {

    @Override
    public Page<WrongQuestion> pageByUser(Long userId, String masteryStatus, long pageNum, long pageSize) {
        LambdaQueryWrapper<WrongQuestion> wrapper = new LambdaQueryWrapper<WrongQuestion>()
                .eq(userId != null, WrongQuestion::getUserId, userId)
                .eq(masteryStatus != null && !masteryStatus.isBlank(), WrongQuestion::getMasteryStatus, masteryStatus)
                .orderByDesc(WrongQuestion::getCreatedAt);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public boolean updateMasteryStatus(Long id, String masteryStatus) {
        WrongQuestion row = this.getById(id);
        if (row == null) {
            return false;
        }
        row.setMasteryStatus(masteryStatus);
        return this.updateById(row);
    }
}
