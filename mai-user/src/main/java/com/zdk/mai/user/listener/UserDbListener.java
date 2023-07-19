package com.zdk.mai.user.listener;

import com.rabbitmq.client.Channel;
import com.zdk.mai.common.core.enums.DynamicTypeEnum;
import com.zdk.mai.common.core.enums.message.MessageTypeEnum;
import com.zdk.mai.common.core.enums.message.NoticeTypeEnum;
import com.zdk.mai.common.core.enums.message.ObjectTypeEnum;
import com.zdk.mai.common.model.dynamic.UserDynamicModel;
import com.zdk.mai.common.model.dynamic.request.DynamicDeleteRequest;
import com.zdk.mai.common.model.follow.UserFollowModel;
import com.zdk.mai.common.model.follow.request.FollowRequest;
import com.zdk.mai.common.model.message.request.MessageCreateRequest;
import com.zdk.mai.common.model.user.vo.UserInfoVO;
import com.zdk.mai.common.rabbit.constant.RabbitConstants;
import com.zdk.mai.common.security.utils.SecurityUtils;
import com.zdk.mai.user.service.IUserDynamicService;
import com.zdk.mai.user.service.IUserFollowService;
import com.zdk.mai.user.service.IUserLevelService;
import com.zdk.mai.user.service.IUserService;
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
 * @Description 用户相关操作消费者
 * @Author zdk
 * @Date 2023/3/22 19:39
 */
@Slf4j
@Component
@RabbitListener(queues = {RabbitConstants.SYNC_DB_QUEUE_USER})
public class UserDbListener {

    @Autowired
    private IUserFollowService userFollowService;

    @Autowired
    private IUserDynamicService userDynamicService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IUserLevelService userLevelService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 更新MySQL关注状态
     * @param followRequest
     * @param channel
     * @param message
     */
    @SneakyThrows
    @RabbitHandler
    @Transactional(rollbackFor = Throwable.class)
    public void syncFollowState(FollowRequest followRequest, Channel channel, Message message){

        Long toUser = followRequest.getToUser();

        Long fromUser = followRequest.getFromUser();

        UserFollowModel one = userFollowService.lambdaQuery()
                .select(UserFollowModel::getFollowId,UserFollowModel::getState)
                .eq(UserFollowModel::getToUser, toUser)
                .eq(UserFollowModel::getFromUser, fromUser).one();

        if (one == null){
            UserFollowModel model = new UserFollowModel();
            BeanUtils.copyProperties(followRequest, model);
            model.setFromUser(fromUser);
            model.setFromUsername(SecurityUtils.getUsername());
            userFollowService.save(model);
            //发消息更新到用户动态(这里的操作用户id是fromUser,被操作对象是toUser)
            UserDynamicModel dynamicModel = new UserDynamicModel(null, fromUser, DynamicTypeEnum.FOLLOW.getType(), toUser,null,true);
            dynamicModel = dynamicModel.build(followRequest);
            rabbitTemplate.convertAndSend(RabbitConstants.SYNC_DATA_EXCHANGE,RabbitConstants.SYNC_DB_ROUTING_KEY_USER,dynamicModel);

            //创建关注消息
            createMessage(followRequest);
        }else {
            // 存在关注记录 更新state即可
            userFollowService.lambdaUpdate()
                    .set(UserFollowModel::getState, !one.getState())
                    .eq(UserFollowModel::getFollowId, one.getFollowId())
                    .update();

            //更新动态
            userDynamicService.lambdaUpdate()
                    .eq(UserDynamicModel::getUserId, fromUser)
                    .eq(UserDynamicModel::getObjectId, toUser)
                    .eq(UserDynamicModel::getType, DynamicTypeEnum.FOLLOW.getType())
                    .setSql("state = !state")
                    .set(UserDynamicModel::getUpdateTime, followRequest.getUpdateTime())
                    .update();

        }

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

    }

    /**
     * 创建关注的消息
     * @param followRequest
     */
    private void createMessage(FollowRequest followRequest){
        //增加站内消息通知
        MessageCreateRequest messageCreateRequest = new MessageCreateRequest();
        messageCreateRequest.setCreateBy(followRequest.getCreateBy());
        messageCreateRequest.setCreateTime(new Date());
        messageCreateRequest.setUpdateBy(followRequest.getUpdateBy());
        messageCreateRequest.setUpdateTime(new Date());
        messageCreateRequest.setSenderId(followRequest.getCreateBy());

        messageCreateRequest.setReceiveId(followRequest.getToUser());
        messageCreateRequest.setObjectType(ObjectTypeEnum.USER.getObjectType());

        UserInfoVO sender = userService.profile(followRequest.getFromUser());
        messageCreateRequest.setObjectId(followRequest.getToUser());
        messageCreateRequest.setSenderId(sender.getUserId());
        messageCreateRequest.setSenderUsername(sender.getUsername());
        messageCreateRequest.setSenderAvatar(sender.getAvatar());
        messageCreateRequest.setMessageType(MessageTypeEnum.FOLLOW.getMessageType());
        messageCreateRequest.setNoticeType(NoticeTypeEnum.SYSTEM_NOTICE.getNoticeType());

        rabbitTemplate.convertAndSend(RabbitConstants.SYNC_DATA_EXCHANGE,RabbitConstants.MESSAGE_DB_ROUTING_KEY, messageCreateRequest);

    }


    /**
     * 插入动态数据
     * @param dynamicModel
     * @param channel
     * @param message
     */
    @SneakyThrows
    @RabbitHandler
    public void syncDynamic(UserDynamicModel dynamicModel, Channel channel, Message message){

        userDynamicService.save(dynamicModel);

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

    }

    /**
     * 动态状态改为不启用
     * @param dynamicDeleteRequest
     * @param channel
     * @param message
     */
    @SneakyThrows
    @RabbitHandler
    public void deleteDynamic(DynamicDeleteRequest dynamicDeleteRequest, Channel channel, Message message){

        Long objectId = dynamicDeleteRequest.getObjectId();
        Long commentId = dynamicDeleteRequest.getCommentId();
        Integer type = dynamicDeleteRequest.getType();
        Date updateTime = dynamicDeleteRequest.getUpdateTime();

        userDynamicService.lambdaUpdate()
                .eq(UserDynamicModel::getObjectId, objectId)
                .eq(commentId != null, UserDynamicModel::getCommentId, commentId)
                .eq(type != null, UserDynamicModel::getType, type)
                .set(UserDynamicModel::getState, false)
                .set(UserDynamicModel::getUpdateTime, updateTime)
                .update();

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

    }

}
