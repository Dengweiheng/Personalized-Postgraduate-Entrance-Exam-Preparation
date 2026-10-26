package com.server.server.module.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.server.server.module.user.entity.SysUser;

public interface SysUserService extends IService<SysUser> {

    Page<SysUser> pageUsers(long pageNum, long pageSize);
}
