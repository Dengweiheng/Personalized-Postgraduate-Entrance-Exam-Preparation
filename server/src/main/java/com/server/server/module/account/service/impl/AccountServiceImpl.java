package com.server.server.module.account.service.impl;

import com.server.server.common.exception.BizException;
import com.server.server.module.account.service.AccountService;
import com.server.server.module.user.entity.SysUser;
import com.server.server.module.user.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username)
                .last("limit 1"));
        if (user == null) {
            throw new BizException("user not found");
        }

        String dbPassword = user.getPassword();
        boolean matched = passwordEncoder.matches(oldPassword, dbPassword);

        if (!matched) {
            throw new BizException("old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        return sysUserMapper.updateById(user) > 0;
    }

    @Override
    public boolean logout(String username) {
        // Basic auth is stateless on the server side; clients should also clear stored credentials.
        SecurityContextHolder.clearContext();
        return username != null && !username.isBlank();
    }
}
