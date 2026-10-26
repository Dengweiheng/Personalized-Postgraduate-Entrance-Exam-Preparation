package com.server.server.module.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.server.common.response.ApiResponse;
import com.server.server.module.user.entity.SysUser;
import com.server.server.module.user.service.SysUserService;
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

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;

    @GetMapping("/page")
    public ApiResponse<Page<SysUser>> page(
            @RequestParam(defaultValue = "1") long pageNum,
            @RequestParam(defaultValue = "10") long pageSize) {
        return ApiResponse.ok(sysUserService.pageUsers(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public ApiResponse<SysUser> getById(@PathVariable Long id) {
        return ApiResponse.ok(sysUserService.getById(id));
    }

    @PostMapping
    public ApiResponse<Boolean> save(@Valid @RequestBody SysUser user) {
        return ApiResponse.ok(sysUserService.save(user));
    }

    @PutMapping
    public ApiResponse<Boolean> update(@RequestBody SysUser user) {
        return ApiResponse.ok(sysUserService.updateById(user));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> remove(@PathVariable Long id) {
        return ApiResponse.ok(sysUserService.removeById(id));
    }
}
