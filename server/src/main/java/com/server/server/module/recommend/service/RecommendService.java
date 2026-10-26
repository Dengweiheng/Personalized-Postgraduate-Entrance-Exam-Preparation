package com.server.server.module.recommend.service;

import com.server.server.module.recommend.dto.RecommendItemVO;
import com.server.server.module.recommend.entity.UserCourseBehavior;

import java.util.List;

public interface RecommendService {

    boolean addBehavior(UserCourseBehavior behavior);

    List<RecommendItemVO> cfRecommend(Long userId, int limit);

    List<RecommendItemVO> contentRecommend(Long userId, int limit);
}
