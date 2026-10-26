package com.server.server.module.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.server.server.common.exception.BizException;
import com.server.server.common.response.ApiResponse;
import com.server.server.module.auth.dto.LoginRequest;
import com.server.server.module.auth.dto.LoginUserVO;
import com.server.server.module.auth.dto.RegisterRequest;
import com.server.server.module.user.entity.SysUser;
import com.server.server.module.user.mapper.SysUserMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ApiResponse<Boolean> register(@Valid @RequestBody RegisterRequest request) {
        SysUser existed = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, request.getUsername())
                .last("limit 1"));
        if (existed != null) {
            throw new BizException("username already exists");
        }

        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setStatus(1);

        return ApiResponse.ok(sysUserMapper.insert(user) > 0);
    }

    @PostMapping("/login")
    public ApiResponse<LoginUserVO> login(@Valid @RequestBody LoginRequest request) {
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, request.getUsername())
                .eq(SysUser::getStatus, 1)
                .last("limit 1"));
        if (user == null) {
            throw new BizException("user not found");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BizException("password incorrect");
        }

        String role = "admin".equalsIgnoreCase(user.getUsername()) ? "ADMIN" : "USER";
        return ApiResponse.ok(new LoginUserVO(user.getId(), user.getUsername(), user.getNickname(), role));
    }
}
