package com.zdk.mai.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zdk.mai.common.model.level.UserLevelModel;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/11 11:57
 */
public interface IUserLevelService extends IService<UserLevelModel> {

    /**
     * 初始化用户身份
     * @param userId
     * @param username
     */
    void create(Long userId,String username);

    /**
     * 根据用户id获取等级信息
     * @param userId
     * @return
     */
    UserLevelModel getByUserId(Long userId);

    /**
     * 更新所有用户的等级信息
     */
    void updateAllUserLevel();

}
