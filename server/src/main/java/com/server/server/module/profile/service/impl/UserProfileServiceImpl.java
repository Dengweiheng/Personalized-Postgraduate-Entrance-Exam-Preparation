package com.server.server.module.profile.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.server.server.module.profile.entity.UserProfile;
import com.server.server.module.profile.mapper.UserProfileMapper;
import com.server.server.module.profile.service.UserProfileService;
import org.springframework.stereotype.Service;

@Service
public class UserProfileServiceImpl extends ServiceImpl<UserProfileMapper, UserProfile> implements UserProfileService {

    @Override
    public UserProfile getByUserId(Long userId) {
        return this.getById(userId);
    }

    @Override
    public boolean saveOrUpdateByUserId(UserProfile profile) {
        return this.saveOrUpdate(profile);
    }
}
