package com.zdk.mai.comment.service.impl;

import com.zdk.mai.comment.mapper.CommentLikeMapper;
import com.zdk.mai.comment.service.ICommentLikeService;
import com.zdk.mai.comment.service.ICommentService;
import com.zdk.mai.common.core.constant.CacheConstants;
import com.zdk.mai.common.core.enums.DynamicTypeEnum;
import com.zdk.mai.common.core.enums.message.MessageTypeEnum;
import com.zdk.mai.common.core.enums.message.NoticeTypeEnum;
import com.zdk.mai.common.core.enums.message.ObjectTypeEnum;
import com.zdk.mai.common.datasource.service.BaseServiceImpl;
import com.zdk.mai.common.model.comment.CommentLikeModel;
import com.zdk.mai.common.model.comment.CommentModel;
import com.zdk.mai.common.model.comment.request.CommentLikeRequest;
import com.zdk.mai.common.model.comment.vo.CommentVO;
import com.zdk.mai.common.model.dynamic.UserDynamicModel;
import com.zdk.mai.common.model.message.request.MessageCreateRequest;
import com.zdk.mai.common.model.user.vo.UserAchievementVO;
import com.zdk.mai.common.rabbit.constant.RabbitConstants;
import com.zdk.mai.common.redis.service.RedisService;
import com.zdk.mai.common.security.utils.SecurityUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/6 16:42
 */
@Service
public class CommentLikeServiceImpl extends BaseServiceImpl<CommentLikeMapper, CommentLikeModel> implements ICommentLikeService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private ICommentService commentService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Resource
    private DefaultRedisScript<Void> commentLikeUpdateScript;

    @CacheEvict(cacheNames = {"comment_like"},key = "'user_comment_like_count:'+#commentLikeRequest.createBy")
    @Override
    public void like(CommentLikeRequest commentLikeRequest) {

        //异步同步like状态
        rabbitTemplate.convertAndSend(RabbitConstants.SYNC_DATA_EXCHANGE, RabbitConstants.SYNC_DB_ROUTING_KEY_COMMENT,commentLikeRequest);

        //更新点赞状态cache
        updateCommentLikeCache(commentLikeRequest);

    }




    /**
     * lua脚本执行更新点赞关系、点赞数量
     * @param commentLikeRequest
     */
    private void updateCommentLikeCache(CommentLikeRequest commentLikeRequest){
        Long commentId = commentLikeRequest.getCommentId();
        Long createBy = commentLikeRequest.getCreateBy();
        Boolean state = commentLikeRequest.getState();

        //参数构建  1,2是更新relation参数 3,4是更新count参数
        List<String> keys = Arrays.asList(CacheConstants.COMMENT_LIKE_RELATION_KEY, getCommentLikeHashKey(commentId, createBy)
        ,CacheConstants.COMMENT_LIKE_COUNT_KEY, String.valueOf(commentId));

        //执行点赞缓存更新
        int stateNum = state ? 1 : 0;
        redisService.execute(commentLikeUpdateScript, keys, stateNum);
    }


    /**
     * 获取评论id:用户id 的hashkey字符串
     * @param commentId
     * @param createId
     * @return
     */
    private String getCommentLikeHashKey(Long commentId,Long createId){
        return commentId + ":" + createId;
    }

    /**
     * 判断用户是否已点赞
     * @param commentId
     * @return
     */
    @Override
    public Boolean userLikeOrNot(Long commentId){
        String hashKey = getCommentLikeHashKey(commentId, SecurityUtils.getUserId());
        Boolean has = redisService.hasCacheMapKey(CacheConstants.COMMENT_LIKE_RELATION_KEY, hashKey);
        if (isOk(has)){
            return has && (Integer) redisService.getCacheMapValue(CacheConstants.COMMENT_LIKE_RELATION_KEY, hashKey) == 1;
        }
        //缓存为空 查MySQL 该用户是否已对此评论点赞 state->true
        CommentLikeModel one = lambdaQuery().select(CommentLikeModel::getState)
                .eq(CommentLikeModel::getCommentId, commentId)
                .eq(CommentLikeModel::getCreateBy, SecurityUtils.getUserId()).one();

        return one != null && one.getState();
    }

    @Override
    public Long getLikeCount(Long commentId) {
        Integer value = redisService.getCacheMapValue(CacheConstants.COMMENT_LIKE_COUNT_KEY, String.valueOf(commentId));
        if (value != null){
            return value.longValue();
        }
        //缓存为空 查MySQL 该条评论的点赞数
        Integer count = lambdaQuery()
                .eq(CommentLikeModel::getCommentId, commentId)
                .eq(CommentLikeModel::getState, true).count();

        return count.longValue();
    }

    @Cacheable(cacheNames = {"comment_like"},key = "'user_comment_like_count:'+#userId")
    @Override
    public UserAchievementVO getUserCommentLikeCount(Long userId) {
        Long commentLikeCount = baseMapper.getUserCommentLikeCount(userId);
        UserAchievementVO vo = new UserAchievementVO();
        vo.setCommentLikeCount(commentLikeCount);
        return vo;
    }
}
