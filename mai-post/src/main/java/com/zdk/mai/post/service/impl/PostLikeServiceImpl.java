package com.zdk.mai.post.service.impl;

import com.zdk.mai.common.core.constant.CacheConstants;
import com.zdk.mai.common.core.enums.DynamicTypeEnum;
import com.zdk.mai.common.datasource.service.BaseServiceImpl;
import com.zdk.mai.common.model.comment.CommentLikeModel;
import com.zdk.mai.common.model.dynamic.UserDynamicModel;
import com.zdk.mai.common.model.post.PostLikeModel;
import com.zdk.mai.common.model.post.request.PostLikeRequest;
import com.zdk.mai.common.rabbit.constant.RabbitConstants;
import com.zdk.mai.common.redis.service.RedisService;
import com.zdk.mai.common.security.utils.SecurityUtils;
import com.zdk.mai.post.mapper.PostLikeMapper;
import com.zdk.mai.post.service.IPostLikeService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/8 16:48
 */
@Service
public class PostLikeServiceImpl extends BaseServiceImpl<PostLikeMapper,PostLikeModel> implements IPostLikeService {

    @Autowired
    private RedisService redisService;

    @Resource
    private DefaultRedisScript<Void> postLikeUpdateScript;

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @CacheEvict(cacheNames = "post_like",key = "'user_post_like_count:'+#postLikeRequest.createBy")
    @Override
    public void like(PostLikeRequest postLikeRequest) {

        rabbitTemplate.convertAndSend(RabbitConstants.SYNC_DATA_EXCHANGE, RabbitConstants.SYNC_DB_ROUTING_KEY_POST, postLikeRequest);

        //更新点赞状态cache
        updatePostLikeCache(postLikeRequest);
    }

    @Override
    public Boolean userLikeOrNot(Long postId) {
        String hashKey = getPostLikeHashKey(postId, SecurityUtils.getUserId());
        Boolean has = redisService.hasCacheMapKey(CacheConstants.POST_LIKE_RELATION_KEY, hashKey);
        if (isOk(has)){
            return has && (Integer) redisService.getCacheMapValue(CacheConstants.POST_LIKE_RELATION_KEY, hashKey) == 1;
        }

        //缓存为空 查MySQL 该用户是否已对此贴子点赞 state->true
        PostLikeModel one = lambdaQuery().select(PostLikeModel::getState)
                .eq(PostLikeModel::getPostId, postId)
                .eq(PostLikeModel::getCreateBy, SecurityUtils.getUserId()).one();

        return one != null && one.getState();

    }

    @Override
    public Long getLikeCount(Long postId) {
        Integer value = redisService.getCacheMapValue(CacheConstants.POST_LIKE_COUNT_KEY, String.valueOf(postId));
        if (value != null){
            return value.longValue();
        }

        //缓存为空 查MySQL 该贴子的点赞数
        Integer count = lambdaQuery()
                .eq(PostLikeModel::getPostId, postId)
                .eq(PostLikeModel::getState, true).count();

        return count.longValue();
    }

    @Cacheable(cacheNames = "post_like",key = "'user_post_like_count:'+#userId")
    @Override
    public Integer getUserPostLikeCount(Long userId) {
        return baseMapper.getUserPostLikeCount(userId);
    }


    /**
     * lua脚本执行更新点赞关系、点赞数量
     * @param postLikeRequest
     */
    private void updatePostLikeCache(PostLikeRequest postLikeRequest){
        Long postId = postLikeRequest.getPostId();
        Long createBy = postLikeRequest.getCreateBy();
        Boolean state = postLikeRequest.getState();

        //参数构建  1,2是更新relation参数 3,4是更新count参数
        List<String> keys = Arrays.asList(CacheConstants.POST_LIKE_RELATION_KEY, getPostLikeHashKey(postId, createBy)
                ,CacheConstants.POST_LIKE_COUNT_KEY, String.valueOf(postId));

        //执行点赞缓存更新
        int stateNum = state ? 1 : 0;
        redisService.execute(postLikeUpdateScript, keys, stateNum);
    }

    /**
     * 获取贴子id:用户id 的hashkey字符串
     * @param postId
     * @param createId
     * @return
     */
    private String getPostLikeHashKey(Long postId,Long createId){
        return postId + ":" + createId;
    }

}
