package com.server.server.module.ability.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.server.server.module.ability.entity.AbilityScore;
import com.server.server.module.ability.mapper.AbilityScoreMapper;
import com.server.server.module.ability.service.AbilityScoreService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AbilityScoreServiceImpl extends ServiceImpl<AbilityScoreMapper, AbilityScore> implements AbilityScoreService {

    @Override
    public AbilityScore latestByUser(Long userId) {
        return this.getOne(new LambdaQueryWrapper<AbilityScore>()
                .eq(AbilityScore::getUserId, userId)
                .orderByDesc(AbilityScore::getStatDate)
                .last("limit 1"));
    }

    @Override
    public List<AbilityScore> historyByUser(Long userId) {
        return this.list(new LambdaQueryWrapper<AbilityScore>()
                .eq(AbilityScore::getUserId, userId)
                .orderByAsc(AbilityScore::getStatDate));
    }
}
