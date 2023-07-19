package com.zdk.mai.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zdk.mai.common.model.follow.UserFollowModel;
import com.zdk.mai.common.model.follow.request.FollowRequest;
import com.zdk.mai.common.model.follow.request.FollowSearchRequest;
import com.zdk.mai.common.model.follow.vo.FollowCountVO;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/11 12:01
 */
public interface IUserFollowService extends IService<UserFollowModel> {


    /**
     * 关注/取消关注 动作
     * @param followRequest
     */
    void follow(FollowRequest followRequest);


    /**
     * 判断当前登陆者是否关注了此用户
     * @param toUser
     * @return
     */
    Boolean isFollow(Long toUser);


    /**
     * 关注列表
     * @param searchRequest
     * @return
     */
    PageInfo<UserFollowModel> list(FollowSearchRequest searchRequest);

    /**
     * 关注统计
     * @param userId
     * @return
     */
    FollowCountVO followCount(Long userId);

}
