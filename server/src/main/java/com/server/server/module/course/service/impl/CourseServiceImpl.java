package com.server.server.module.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.server.server.module.course.dto.CourseCardVO;
import com.server.server.module.course.entity.Course;
import com.server.server.module.course.mapper.CourseMapper;
import com.server.server.module.course.service.CourseService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    @Override
    public List<String> listCategories() {
        List<Course> courses = this.list(new LambdaQueryWrapper<Course>()
                .eq(Course::getStatus, 1)
                .select(Course::getSubject)
                .orderByAsc(Course::getSubject));

        Set<String> set = new LinkedHashSet<>();
        for (Course course : courses) {
            if (course.getSubject() != null && !course.getSubject().isBlank()) {
                set.add(course.getSubject());
            }
        }
        return new ArrayList<>(set);
    }

    @Override
    public Page<CourseCardVO> pageCards(String category, long pageNum, long pageSize) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<Course>()
                .eq(Course::getStatus, 1)
                .eq(category != null && !category.isBlank(), Course::getSubject, category)
                .orderByDesc(Course::getCreatedAt);

        Page<Course> page = this.page(new Page<>(pageNum, pageSize), wrapper);

        Page<CourseCardVO> result = new Page<>(pageNum, pageSize, page.getTotal());
        List<CourseCardVO> records = new ArrayList<>();
        for (Course course : page.getRecords()) {
            CourseCardVO vo = new CourseCardVO();
            BeanUtils.copyProperties(course, vo);
            records.add(vo);
        }
        result.setRecords(records);
        return result;
    }
}
