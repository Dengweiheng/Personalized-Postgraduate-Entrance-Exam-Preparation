package com.server.server.module.ability.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.server.server.module.ability.entity.AbilityScore;

import java.util.List;

public interface AbilityScoreService extends IService<AbilityScore> {

    AbilityScore latestByUser(Long userId);

    List<AbilityScore> historyByUser(Long userId);
}
