package com.zdk.mai.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zdk.mai.common.core.constant.CacheConstants;
import com.zdk.mai.common.datasource.service.BaseServiceImpl;
import com.zdk.mai.common.model.follow.UserFollowModel;
import com.zdk.mai.common.model.follow.request.FollowRequest;
import com.zdk.mai.common.model.follow.request.FollowSearchRequest;
import com.zdk.mai.common.model.follow.vo.FollowCountVO;
import com.zdk.mai.common.model.follow.vo.FollowInfoVO;
import com.zdk.mai.common.rabbit.constant.RabbitConstants;
import com.zdk.mai.common.redis.service.RedisService;
import com.zdk.mai.common.security.utils.SecurityUtils;
import com.zdk.mai.user.mapper.UserFollowMapper;
import com.zdk.mai.user.service.IUserFollowService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/11 12:02
 */
@Service
public class UserFollowServiceImpl extends BaseServiceImpl<UserFollowMapper, UserFollowModel> implements IUserFollowService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private DefaultRedisScript<Void> userFollowUpdateScript;


    @Override
    public void follow(FollowRequest followRequest) {

        //防止多线程userId丢失
        followRequest.setFromUser(SecurityUtils.getUserId());

        // 异步更新MySQL状态
        rabbitTemplate.convertAndSend(RabbitConstants.SYNC_DATA_EXCHANGE,RabbitConstants.SYNC_DB_ROUTING_KEY_USER,followRequest);

        //更新缓存状态
        updateFollowState(followRequest);
    }

    /**
     * lua更新redis关注状态相关缓存
     * @param followRequest
     */
    private void updateFollowState(FollowRequest followRequest){
        Long toUser = followRequest.getToUser();

        Long fromUser = SecurityUtils.getUserId();

        //toUser:fromUser = 1(0);fromUser关注数+(-)1; toUser粉丝数+(-)1;
        String followRelationHashKey = toUser + ":" + fromUser;

        List<String> keys = Arrays.asList(CacheConstants.USER_FOLLOW_RELATION_KEY, followRelationHashKey
                ,CacheConstants.USER_FOLLOW_COUNT_KEY, String.valueOf(fromUser)
                ,CacheConstants.USER_FAN_COUNT_KEY, String.valueOf(toUser));

        redisService.execute(userFollowUpdateScript, keys);

    }

    @Override
    public Boolean isFollow(Long toUser) {
        Long userId = SecurityUtils.getUserId();

        String followRelationHashKey = toUser + ":" + userId;
        Boolean has = redisService.hasCacheMapKey(CacheConstants.USER_FOLLOW_RELATION_KEY, followRelationHashKey);
        if (isOk(has)){
            return has && (Integer) redisService.getCacheMapValue(CacheConstants.USER_FOLLOW_RELATION_KEY, followRelationHashKey) == 1;
        }
        // 缓存为空查MySQL
        UserFollowModel one = lambdaQuery().select(UserFollowModel::getState)
                .eq(UserFollowModel::getToUser, toUser)
                .eq(UserFollowModel::getFromUser, userId).one();

        return one != null && one.getState();
    }

    @Override
    public PageInfo<UserFollowModel> list(FollowSearchRequest searchRequest) {
        Long userId = searchRequest.getUserId();
        Integer type = searchRequest.getType();
        PageHelper.startPage(searchRequest.getCurrentPage(), searchRequest.getPageSize());
        List<UserFollowModel> list = lambdaQuery()
                .eq(type == 1, UserFollowModel::getFromUser, userId)
                .eq(type == 2, UserFollowModel::getToUser, userId)
                .eq(UserFollowModel::getState, true)
                .orderByDesc(UserFollowModel::getUpdateTime).list();

        return new PageInfo<>(list);
    }

    @Override
    public FollowCountVO followCount(Long userId) {
        Integer followCount,fanCount;

        followCount = redisService.getCacheMapValue(CacheConstants.USER_FOLLOW_COUNT_KEY, String.valueOf(userId));
        fanCount = redisService.getCacheMapValue(CacheConstants.USER_FAN_COUNT_KEY, String.valueOf(userId));
        //空的查库
        if (followCount == null){
            QueryWrapper<UserFollowModel> followQuery = new QueryWrapper<>();
            followQuery.eq("from_user", userId);
            followQuery.eq("state", true);
            followCount = baseMapper.selectCount(followQuery);

        }
        if (fanCount == null){
            QueryWrapper<UserFollowModel> fanQuery = new QueryWrapper<>();
            fanQuery.eq("to_user", userId);
            fanQuery.eq("state", true);
            fanCount = baseMapper.selectCount(fanQuery);
        }
        return new FollowCountVO(followCount.longValue(),fanCount.longValue());
    }


}
