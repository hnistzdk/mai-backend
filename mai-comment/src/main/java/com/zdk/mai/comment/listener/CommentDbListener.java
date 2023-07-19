package com.zdk.mai.comment.listener;

import com.rabbitmq.client.Channel;
import com.zdk.mai.api.user.RemoteUserService;
import com.zdk.mai.comment.service.ICommentLikeService;
import com.zdk.mai.comment.service.ICommentService;
import com.zdk.mai.common.core.enums.DynamicTypeEnum;
import com.zdk.mai.common.core.enums.message.MessageTypeEnum;
import com.zdk.mai.common.core.enums.message.NoticeTypeEnum;
import com.zdk.mai.common.core.enums.message.ObjectTypeEnum;
import com.zdk.mai.common.model.comment.CommentLikeModel;
import com.zdk.mai.common.model.comment.request.CommentLikeRequest;
import com.zdk.mai.common.model.comment.vo.CommentVO;
import com.zdk.mai.common.model.dynamic.UserDynamicModel;
import com.zdk.mai.common.model.dynamic.request.DynamicDeleteRequest;
import com.zdk.mai.common.model.message.request.MessageCreateRequest;
import com.zdk.mai.common.model.post.PostModel;
import com.zdk.mai.common.model.user.vo.UserInfoVO;
import com.zdk.mai.common.rabbit.constant.RabbitConstants;
import com.zdk.mai.common.redis.service.RedisService;
import com.zdk.mai.common.security.utils.SecurityUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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
 * @Date 2023/3/23 14:39
 */
@Slf4j
@Component
@RabbitListener(queues = {RabbitConstants.SYNC_DB_QUEUE_COMMENT})
public class CommentDbListener {

    @Autowired
    private ICommentLikeService commentLikeService;

    @Autowired
    private ICommentService commentService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RemoteUserService remoteUserService;

    /**
     * 同步点赞状态到MySQL
     * @param commentLikeRequest
     * @param channel
     * @param message
     */
    @SneakyThrows
    @RabbitHandler
    @Transactional(rollbackFor = Throwable.class)
    public void syncCommentLikeToDb(CommentLikeRequest commentLikeRequest, Channel channel, Message message){

        Long commentId = commentLikeRequest.getCommentId();
        Long createBy = commentLikeRequest.getCreateBy();

        CommentLikeModel commentLikeModel = commentLikeService.lambdaQuery()
                .eq(CommentLikeModel::getCommentId, commentId)
                .eq(CommentLikeModel::getCreateBy, createBy)
                .one();
        if (commentLikeModel == null){
            CommentLikeModel model = new CommentLikeModel();
            BeanUtils.copyProperties(commentLikeRequest, model);
            model.setState(!commentLikeRequest.getState());
            commentLikeService.save(model);

            //发消息更新到用户动态(这里的objectId是postId)
            CommentVO comment = commentService.getById(commentId);
            UserDynamicModel dynamicModel = new UserDynamicModel(null, createBy, DynamicTypeEnum.COMMENT_LIKE.getType(), comment.getPostId(),commentId,true);
            dynamicModel = dynamicModel.build(commentLikeRequest);
            rabbitTemplate.convertAndSend(RabbitConstants.SYNC_DATA_EXCHANGE,RabbitConstants.SYNC_DB_ROUTING_KEY_USER,dynamicModel);

            //发消息创建通知
            createMessage(commentLikeRequest);
        }else {
            //更新点赞状态
            commentLikeService.lambdaUpdate()
                    .set(CommentLikeModel::getState, !commentLikeRequest.getState())
                    .eq(CommentLikeModel::getCommentLikeId, commentLikeModel.getCommentLikeId())
                    .update();
            // 当前是true要改为false
            if (commentLikeRequest.getState()){
                //删动态
                CommentVO comment = commentService.getById(commentId);
                DynamicDeleteRequest dynamicDeleteRequest = new DynamicDeleteRequest();
                dynamicDeleteRequest.setObjectId(comment.getPostId());
                dynamicDeleteRequest.setCommentId(commentId);
                dynamicDeleteRequest.setType(DynamicTypeEnum.COMMENT_LIKE.getType());
                dynamicDeleteRequest.setUpdateTime(commentLikeRequest.getUpdateTime());
                rabbitTemplate.convertAndSend(RabbitConstants.SYNC_DATA_EXCHANGE, RabbitConstants.SYNC_DB_ROUTING_KEY_USER, dynamicDeleteRequest);
            }
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

    }

    /**
     * 新增点赞评论的消息
     * @param commentLikeRequest
     */
    private void createMessage(CommentLikeRequest commentLikeRequest){
        //增加站内消息通知
        MessageCreateRequest messageCreateRequest = new MessageCreateRequest();
        messageCreateRequest.setCreateBy(commentLikeRequest.getCreateBy());
        messageCreateRequest.setCreateTime(new Date());
        messageCreateRequest.setUpdateBy(commentLikeRequest.getUpdateBy());
        messageCreateRequest.setUpdateTime(new Date());
        messageCreateRequest.setSenderId(commentLikeRequest.getCreateBy());

        CommentVO commentVO = commentService.getById(commentLikeRequest.getCommentId());
        messageCreateRequest.setReceiveId(commentVO.getCreateBy());
        messageCreateRequest.setObjectType(ObjectTypeEnum.COMMENT.getObjectType());

        messageCreateRequest.setObjectId(commentVO.getPostId());
        messageCreateRequest.setCommentId(commentVO.getCommentId());
        messageCreateRequest.setContent(commentVO.getContent());
        UserInfoVO sender = remoteUserService.profile(commentLikeRequest.getCreateBy()).getData();
        messageCreateRequest.setSenderUsername(sender.getUsername());
        messageCreateRequest.setSenderAvatar(sender.getAvatar());
        messageCreateRequest.setMessageType(MessageTypeEnum.LIKE.getMessageType());
        messageCreateRequest.setNoticeType(NoticeTypeEnum.SYSTEM_NOTICE.getNoticeType());

        rabbitTemplate.convertAndSend(RabbitConstants.SYNC_DATA_EXCHANGE,RabbitConstants.MESSAGE_DB_ROUTING_KEY, messageCreateRequest);

    }

}
