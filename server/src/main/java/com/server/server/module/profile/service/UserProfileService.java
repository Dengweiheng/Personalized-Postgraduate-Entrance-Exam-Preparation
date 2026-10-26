package com.server.server.module.profile.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.server.server.module.profile.entity.UserProfile;

public interface UserProfileService extends IService<UserProfile> {

    UserProfile getByUserId(Long userId);

    boolean saveOrUpdateByUserId(UserProfile profile);
}
