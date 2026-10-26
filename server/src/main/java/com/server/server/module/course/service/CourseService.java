package com.server.server.module.course.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.server.server.module.course.dto.CourseCardVO;
import com.server.server.module.course.entity.Course;

import java.util.List;

public interface CourseService extends IService<Course> {

    List<String> listCategories();

    Page<CourseCardVO> pageCards(String category, long pageNum, long pageSize);
}
