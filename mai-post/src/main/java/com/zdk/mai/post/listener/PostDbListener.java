package com.zdk.mai.post.listener;

import com.rabbitmq.client.Channel;
import com.zdk.mai.api.user.RemoteUserService;
import com.zdk.mai.common.core.constant.CacheConstants;
import com.zdk.mai.common.core.enums.DynamicTypeEnum;
import com.zdk.mai.common.core.enums.message.MessageTypeEnum;
import com.zdk.mai.common.core.enums.message.NoticeTypeEnum;
import com.zdk.mai.common.core.enums.message.ObjectTypeEnum;
import com.zdk.mai.common.model.comment.request.CommentLikeRequest;
import com.zdk.mai.common.model.comment.vo.CommentVO;
import com.zdk.mai.common.model.dynamic.UserDynamicModel;
import com.zdk.mai.common.model.dynamic.request.DynamicDeleteRequest;
import com.zdk.mai.common.model.message.request.MessageCreateRequest;
import com.zdk.mai.common.model.post.PostLikeModel;
import com.zdk.mai.common.model.post.PostModel;
import com.zdk.mai.common.model.post.request.PostLikeRequest;
import com.zdk.mai.common.model.user.vo.UserInfoVO;
import com.zdk.mai.common.rabbit.constant.RabbitConstants;
import com.zdk.mai.common.redis.service.RedisService;
import com.zdk.mai.common.redis.service.RedissonService;
import com.zdk.mai.common.security.service.TokenService;
import com.zdk.mai.common.security.utils.SecurityUtils;
import com.zdk.mai.post.service.IPostLikeService;
import com.zdk.mai.post.service.IPostService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/23 14:13
 */
@Slf4j
@Component
@RabbitListener(queues = {RabbitConstants.SYNC_DB_QUEUE_POST})
public class PostDbListener {

    @Autowired
    private IPostService postService;

    @Autowired
    private IPostLikeService postLikeService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedissonService redissonService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RemoteUserService remoteUserService;

    /**
     * 同步pv
     * @param postId
     * @param channel
     * @param message
     */
    @SneakyThrows
    @RabbitHandler
    public void syncPvToDb(Long postId, Channel channel, Message message){

        // TODO: 2023/3/23 这里可以10个更新一次  redisson加锁处理

        log.info("更新pv：" + postId);
        RLock lock = redissonService.lock("incrPostPv");
        try {

            Integer count = redisService.getCacheObject(CacheConstants.PV_INCR_COUNT + postId);
            if (count == null){
                count = 0;
            }
            log.info("count:->{}", count);
            redisService.setCacheObject(CacheConstants.PV_INCR_COUNT + postId, ++count);
            //如果累计的pv大于等于10次了
            if (count >= CacheConstants.PV_INCR_MAX_COUNT){
                //更新db
                postService.lambdaUpdate().setSql("pv = pv + "+count).eq(PostModel::getPostId,postId).update();

                //更新缓存
                redisService.deleteObject(CacheConstants.PV_INCR_COUNT + postId);
            }

        }catch (Exception e){
            log.error("累计更新贴子pv失败->{}",postId);
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        redissonService.unlock(lock);

    }

    @SneakyThrows
    @RabbitHandler
    @Transactional(rollbackFor = Throwable.class)
    public void syncPostLikeToDb(PostLikeRequest postLikeRequest, Channel channel, Message message){

        log.info("更新贴子点赞：" + postLikeRequest.getPostId());

        Long postId = postLikeRequest.getPostId();
        Long createBy = postLikeRequest.getCreateBy();

        PostLikeModel postLikeModel = postLikeService.lambdaQuery()
                .eq(PostLikeModel::getPostId, postId)
                .eq(PostLikeModel::getCreateBy, createBy)
                .one();

        if (postLikeModel == null){
            PostLikeModel model = new PostLikeModel();
            BeanUtils.copyProperties(postLikeRequest, model);
            //因为传来的是未修改前的状态
            model.setState(!postLikeRequest.getState());
            postLikeService.save(model);

            //发消息更新到用户动态(这里的objectId也是postId)
            UserDynamicModel dynamicModel = new UserDynamicModel(null, postLikeRequest.getCreateBy(), DynamicTypeEnum.POST_LIKE.getType(), model.getPostId(),null,true);
            dynamicModel = dynamicModel.build(postLikeRequest);
            rabbitTemplate.convertAndSend(RabbitConstants.SYNC_DATA_EXCHANGE,RabbitConstants.SYNC_DB_ROUTING_KEY_USER,dynamicModel);
            createMessage(postLikeRequest);
        }else {
            //更新点赞状态
            log.info("postLikeRequest->{}", postLikeRequest);
            postLikeService.lambdaUpdate()
                    .set(PostLikeModel::getState, !postLikeModel.getState())
                    .eq(PostLikeModel::getPostLikeId, postLikeModel.getPostLikeId())
                    .update();

            //当前的是true 表示要要删除相关动态
            if (postLikeModel.getState()){
                DynamicDeleteRequest dynamicDeleteRequest = new DynamicDeleteRequest();
                dynamicDeleteRequest.setObjectId(postId);
                dynamicDeleteRequest.setType(DynamicTypeEnum.POST_LIKE.getType());
                dynamicDeleteRequest.setUpdateTime(new Date());
                rabbitTemplate.convertAndSend(RabbitConstants.SYNC_DATA_EXCHANGE, RabbitConstants.SYNC_DB_ROUTING_KEY_USER, dynamicDeleteRequest);
            }
        }

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 新增点赞贴子的消息
     * @param postLikeRequest
     */
    private void createMessage(PostLikeRequest postLikeRequest){
        //增加站内消息通知
        MessageCreateRequest messageCreateRequest = new MessageCreateRequest();
        messageCreateRequest.setCreateBy(postLikeRequest.getCreateBy());
        messageCreateRequest.setCreateTime(new Date());
        messageCreateRequest.setUpdateBy(postLikeRequest.getUpdateBy());
        messageCreateRequest.setUpdateTime(new Date());
        messageCreateRequest.setSenderId(postLikeRequest.getCreateBy());

        PostModel postModel = postService.getById(postLikeRequest.getPostId());

        messageCreateRequest.setReceiveId(postModel.getCreateBy());
        messageCreateRequest.setObjectType(ObjectTypeEnum.POST.getObjectType());

        messageCreateRequest.setObjectId(postModel.getPostId());
        messageCreateRequest.setContent(postModel.getTitle() != null ? postModel.getTitle() : postModel.getContent());

        UserInfoVO sender = remoteUserService.profile(postLikeRequest.getCreateBy()).getData();
        messageCreateRequest.setSenderUsername(sender.getUsername());
        messageCreateRequest.setSenderAvatar(sender.getAvatar());
        messageCreateRequest.setMessageType(MessageTypeEnum.LIKE.getMessageType());
        messageCreateRequest.setNoticeType(NoticeTypeEnum.SYSTEM_NOTICE.getNoticeType());

        rabbitTemplate.convertAndSend(RabbitConstants.SYNC_DATA_EXCHANGE,RabbitConstants.MESSAGE_DB_ROUTING_KEY, messageCreateRequest);

    }

}
