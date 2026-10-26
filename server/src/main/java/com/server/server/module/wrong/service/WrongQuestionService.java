package com.server.server.module.wrong.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.server.server.module.wrong.entity.WrongQuestion;

public interface WrongQuestionService extends IService<WrongQuestion> {

    Page<WrongQuestion> pageByUser(Long userId, String masteryStatus, long pageNum, long pageSize);

    boolean updateMasteryStatus(Long id, String masteryStatus);
}
