package com.server.server.module.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.server.common.exception.BizException;
import com.server.server.common.response.ApiResponse;
import com.server.server.module.course.entity.Course;
import com.server.server.module.course.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/courses")
@RequiredArgsConstructor
public class AdminCourseController {

    private final CourseService courseService;

    @GetMapping("/page")
    public ApiResponse<Page<Course>> page(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String subject,
            @RequestParam(defaultValue = "1") long pageNum,
            @RequestParam(defaultValue = "10") long pageSize) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<Course>()
                .like(keyword != null && !keyword.isBlank(), Course::getTitle, keyword)
                .eq(subject != null && !subject.isBlank(), Course::getSubject, subject)
                .orderByDesc(Course::getCreatedAt);
        return ApiResponse.ok(courseService.page(new Page<>(pageNum, pageSize), wrapper));
    }

    @PostMapping
    public ApiResponse<Boolean> add(@Valid @RequestBody Course course) {
        if (course.getStatus() == null) {
            course.setStatus(1);
        }
        return ApiResponse.ok(courseService.save(course));
    }

    @PutMapping
    public ApiResponse<Boolean> update(@RequestBody Course course) {
        return ApiResponse.ok(courseService.updateById(course));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.ok(courseService.removeById(id));
    }

    @PostMapping("/upload-cover")
    public ApiResponse<String> uploadCover(@RequestParam("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BizException("请先选择封面图片");
        }

        Path dir = Paths.get(System.getProperty("user.dir"), "uploads").toAbsolutePath().normalize();
        Files.createDirectories(dir);

        String ext = "";
        String filename = file.getOriginalFilename();
        if (filename != null && filename.contains(".")) {
            ext = filename.substring(filename.lastIndexOf('.'));
        }

        String storedName = UUID.randomUUID() + ext;
        Path target = dir.resolve(storedName);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return ApiResponse.ok("/uploads/" + storedName);
    }
}
