package com.server.server.module.recommend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.server.server.module.course.entity.Course;
import com.server.server.module.course.mapper.CourseMapper;
import com.server.server.module.recommend.dto.RecommendItemVO;
import com.server.server.module.recommend.entity.RecommendationResult;
import com.server.server.module.recommend.entity.UserCourseBehavior;
import com.server.server.module.recommend.entity.UserProfileLite;
import com.server.server.module.recommend.mapper.RecommendationResultMapper;
import com.server.server.module.recommend.mapper.UserCourseBehaviorMapper;
import com.server.server.module.recommend.mapper.UserProfileLiteMapper;
import com.server.server.module.recommend.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendServiceImpl implements RecommendService {

    private final UserCourseBehaviorMapper behaviorMapper;
    private final UserProfileLiteMapper profileMapper;
    private final CourseMapper courseMapper;
    private final RecommendationResultMapper recommendationResultMapper;

    @Override
    public boolean addBehavior(UserCourseBehavior behavior) {
        if (behavior.getScore() == null) {
            behavior.setScore(1);
        }
        return behaviorMapper.insert(behavior) > 0;
    }

    @Override
    public List<RecommendItemVO> cfRecommend(Long userId, int limit) {
        List<UserCourseBehavior> myBehavior = behaviorMapper.selectList(
                new LambdaQueryWrapper<UserCourseBehavior>().eq(UserCourseBehavior::getUserId, userId));
        if (myBehavior.isEmpty()) {
            return List.of();
        }

        Set<Long> myCourseIds = myBehavior.stream().map(UserCourseBehavior::getCourseId).collect(Collectors.toSet());

        List<UserCourseBehavior> allBehavior = behaviorMapper.selectList(new LambdaQueryWrapper<>());

        Map<Long, Set<Long>> userCourseMap = new HashMap<>();
        for (UserCourseBehavior b : allBehavior) {
            userCourseMap.computeIfAbsent(b.getUserId(), k -> new HashSet<>()).add(b.getCourseId());
        }

        Map<Long, Integer> similarityMap = new HashMap<>();
        for (Map.Entry<Long, Set<Long>> entry : userCourseMap.entrySet()) {
            Long otherUserId = entry.getKey();
            if (otherUserId.equals(userId)) {
                continue;
            }
            int overlap = 0;
            for (Long courseId : entry.getValue()) {
                if (myCourseIds.contains(courseId)) {
                    overlap++;
                }
            }
            if (overlap > 0) {
                similarityMap.put(otherUserId, overlap);
            }
        }

        List<Long> similarUsers = similarityMap.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(5)
                .map(Map.Entry::getKey)
                .toList();

        if (similarUsers.isEmpty()) {
            List<RecommendItemVO> fallback = fallbackRecommend(myCourseIds, limit);
            saveResults(userId, "CF", fallback);
            return fallback;
        }

        List<UserCourseBehavior> similarBehavior = behaviorMapper.selectList(
                new LambdaQueryWrapper<UserCourseBehavior>().in(UserCourseBehavior::getUserId, similarUsers));

        Map<Long, Integer> courseScore = new HashMap<>();
        for (UserCourseBehavior b : similarBehavior) {
            if (myCourseIds.contains(b.getCourseId())) {
                continue;
            }
            int val = b.getScore() == null ? 1 : b.getScore();
            courseScore.put(b.getCourseId(), courseScore.getOrDefault(b.getCourseId(), 0) + val);
        }

        List<Map.Entry<Long, Integer>> topCourses = courseScore.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(limit)
                .toList();

        List<RecommendItemVO> result = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : topCourses) {
            Course course = courseMapper.selectById(entry.getKey());
            if (course == null || course.getStatus() == null || course.getStatus() != 1) {
                continue;
            }
            RecommendItemVO vo = buildVo(course, BigDecimal.valueOf(entry.getValue()), "相似用户学过这门课");
            result.add(vo);
        }

        if (result.isEmpty()) {
            result = fallbackRecommend(myCourseIds, limit);
        }

        saveResults(userId, "CF", result);
        return result;
    }

    @Override
    public List<RecommendItemVO> contentRecommend(Long userId, int limit) {
        UserProfileLite profile = profileMapper.selectById(userId);
        List<UserCourseBehavior> myBehavior = behaviorMapper.selectList(
                new LambdaQueryWrapper<UserCourseBehavior>().eq(UserCourseBehavior::getUserId, userId));
        Set<Long> myCourseIds = myBehavior.stream().map(UserCourseBehavior::getCourseId).collect(Collectors.toSet());
        List<Course> courses = courseMapper.selectList(new LambdaQueryWrapper<Course>()
                .eq(Course::getStatus, 1));

        if (courses.isEmpty()) {
            return List.of();
        }

        Set<String> interestTags = split(profile == null ? null : profile.getInterestTags());
        Set<String> weakSubjects = split(profile == null ? null : profile.getWeakSubjects());
        Set<String> learnedSubjects = learnedSubjects(myCourseIds);

        List<RecommendItemVO> list = new ArrayList<>();
        for (Course c : courses) {
            if (myCourseIds.contains(c.getId())) {
                continue;
            }
            BigDecimal score = BigDecimal.ZERO;
            StringBuilder reason = new StringBuilder();

            Set<String> courseTags = split(c.getTags());
            int tagMatch = 0;
            for (String t : courseTags) {
                if (interestTags.contains(t)) {
                    tagMatch++;
                }
            }
            if (tagMatch > 0) {
                score = score.add(BigDecimal.valueOf(tagMatch * 2.0));
                reason.append("匹配兴趣标签;");
            }

            if (c.getSubject() != null && weakSubjects.contains(c.getSubject())) {
                score = score.add(BigDecimal.valueOf(3));
                reason.append("匹配薄弱科目;");
            }

            if (c.getSubject() != null && learnedSubjects.contains(c.getSubject())) {
                score = score.add(BigDecimal.valueOf(2));
                reason.append("基于已学科目延伸;");
            }

            if (score.compareTo(BigDecimal.ZERO) > 0) {
                list.add(buildVo(c, score.setScale(2, RoundingMode.HALF_UP), reason.toString()));
            }
        }

        List<RecommendItemVO> result = list.stream()
                .sorted(Comparator.comparing(RecommendItemVO::getScore).reversed())
                .limit(limit)
                .toList();

        if (result.isEmpty()) {
            result = fallbackRecommend(myCourseIds, limit);
        }

        saveResults(userId, "CONTENT", result);
        return result;
    }

    private List<RecommendItemVO> fallbackRecommend(Set<Long> learnedCourseIds, int limit) {
        Set<String> learnedSubjects = learnedSubjects(learnedCourseIds);

        List<UserCourseBehavior> allBehavior = behaviorMapper.selectList(new LambdaQueryWrapper<>());
        Map<Long, Integer> hotMap = new HashMap<>();
        for (UserCourseBehavior b : allBehavior) {
            int val = b.getScore() == null ? 1 : b.getScore();
            hotMap.put(b.getCourseId(), hotMap.getOrDefault(b.getCourseId(), 0) + val);
        }

        List<Course> allCourses = courseMapper.selectList(new LambdaQueryWrapper<Course>()
                .eq(Course::getStatus, 1)
                .orderByDesc(Course::getCreatedAt));

        List<RecommendItemVO> items = new ArrayList<>();
        for (Course c : allCourses) {
            if (learnedCourseIds.contains(c.getId())) {
                continue;
            }
            int hot = hotMap.getOrDefault(c.getId(), 0);
            boolean sameSubject = c.getSubject() != null && learnedSubjects.contains(c.getSubject());
            int score = hot + (sameSubject ? 3 : 0);

            if (score <= 0 && !sameSubject) {
                continue;
            }

            String reason;
            if (sameSubject && hot > 0) {
                reason = "同学科学习延伸 + 热门课程";
            } else if (sameSubject) {
                reason = "同学科学习延伸";
            } else {
                reason = "近期热门学习课程";
            }
            items.add(buildVo(c, BigDecimal.valueOf(score), reason));
        }

        if (items.isEmpty()) {
            for (Course c : allCourses) {
                if (learnedCourseIds.contains(c.getId())) {
                    continue;
                }
                items.add(buildVo(c, BigDecimal.ONE, "为你探索的新课程"));
                if (items.size() >= limit) {
                    break;
                }
            }
            return items;
        }

        return items.stream()
                .sorted(Comparator.comparing(RecommendItemVO::getScore).reversed())
                .limit(limit)
                .toList();
    }

    private Set<String> learnedSubjects(Set<Long> learnedCourseIds) {
        if (learnedCourseIds == null || learnedCourseIds.isEmpty()) {
            return Set.of();
        }
        return courseMapper.selectBatchIds(learnedCourseIds).stream()
                .map(Course::getSubject)
                .filter(v -> v != null && !v.isBlank())
                .collect(Collectors.toSet());
    }

    private RecommendItemVO buildVo(Course course, BigDecimal score, String reason) {
        RecommendItemVO vo = new RecommendItemVO();
        vo.setCourseId(course.getId());
        vo.setTitle(course.getTitle());
        vo.setSubject(course.getSubject());
        vo.setDifficulty(course.getDifficulty());
        vo.setTags(course.getTags());
        vo.setCoverUrl(course.getCoverUrl());
        vo.setScore(score);
        vo.setReason(reason);
        return vo;
    }

    private Set<String> split(String s) {
        if (s == null || s.isBlank()) {
            return Set.of();
        }
        return List.of(s.split(",")).stream()
                .map(String::trim)
                .filter(v -> !v.isBlank())
                .collect(Collectors.toSet());
    }

    private void saveResults(Long userId, String algorithmType, List<RecommendItemVO> items) {
        recommendationResultMapper.delete(new LambdaQueryWrapper<RecommendationResult>()
                .eq(RecommendationResult::getUserId, userId)
                .eq(RecommendationResult::getAlgorithmType, algorithmType));

        for (RecommendItemVO item : items) {
            RecommendationResult row = new RecommendationResult();
            row.setUserId(userId);
            row.setAlgorithmType(algorithmType);
            row.setCourseId(item.getCourseId());
            row.setScore(item.getScore());
            row.setReasonText(item.getReason());
            row.setCreatedAt(LocalDateTime.now());
            recommendationResultMapper.insert(row);
        }
    }
}
