package com.zdk.mai.message.listener;

import cn.hutool.core.collection.CollectionUtil;
import com.rabbitmq.client.Channel;
import com.zdk.mai.common.core.constant.CacheConstants;
import com.zdk.mai.common.core.enums.message.MessageTypeEnum;
import com.zdk.mai.common.model.message.MessageInfoModel;
import com.zdk.mai.common.model.message.MessageNoticeModel;
import com.zdk.mai.common.model.message.request.MessageCreateRequest;
import com.zdk.mai.common.model.message.request.MessageDeleteRequest;
import com.zdk.mai.common.rabbit.constant.RabbitConstants;
import com.zdk.mai.common.redis.service.RedisService;
import com.zdk.mai.message.service.IMessageInfoService;
import com.zdk.mai.message.service.IMessageNoticeService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description 监听消息的创建
 * @Author zdk
 * @Date 2023/3/27 20:18
 */
@Slf4j
@Component
@RabbitListener(queues = {RabbitConstants.MESSAGE_DB_QUEUE})
public class MessageListener {

    @Autowired
    private IMessageInfoService messageInfoService;

    @Autowired
    private IMessageNoticeService messageNoticeService;

    @Autowired
    private RedisService redisService;


    @SneakyThrows
    @RabbitHandler
    @Transactional(rollbackFor = Throwable.class)
    public void createMessage(MessageCreateRequest messageCreateRequest, Channel channel, Message message){

        log.info("创建消息通知:-> {}", messageCreateRequest);

        MessageInfoModel messageInfoModel = new MessageInfoModel();
        BeanUtils.copyProperties(messageCreateRequest, messageInfoModel);
        messageInfoService.save(messageInfoModel);

        MessageNoticeModel messageNoticeModel = new MessageNoticeModel();
        BeanUtils.copyProperties(messageCreateRequest, messageNoticeModel);
        messageNoticeModel.setMessageId(messageInfoModel.getMessageId());
        messageNoticeService.save(messageNoticeModel);

        //缓存未读消息数量
        Long receiveId = messageCreateRequest.getReceiveId();
        Integer messageType = messageCreateRequest.getMessageType();
        if (messageType.equals(MessageTypeEnum.REPLY.getMessageType())){
            redisService.incr(CacheConstants.MESSAGE_NOT_READ_COUNT_REPLY + receiveId);
        }
        else if (messageType.equals(MessageTypeEnum.LIKE.getMessageType())){
            redisService.incr(CacheConstants.MESSAGE_NOT_READ_COUNT_LIKE + receiveId);
        }
        else if (messageType.equals(MessageTypeEnum.FOLLOW.getMessageType())){
            redisService.incr(CacheConstants.MESSAGE_NOT_READ_COUNT_FOLLOW + receiveId);
        }
        else if (messageType.equals(MessageTypeEnum.SYSTEM.getMessageType())){
            redisService.incr(CacheConstants.MESSAGE_NOT_READ_COUNT_SYSTEM + receiveId);
        }
        redisService.incr(CacheConstants.MESSAGE_NOT_READ_COUNT_ALL + receiveId);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

    }

    @SneakyThrows
    @RabbitHandler
    @Transactional(rollbackFor = Throwable.class)
    public void deleteMessage(MessageDeleteRequest messageDeleteRequest, Channel channel, Message message){

        log.info("删除消息通知:-> {}", messageDeleteRequest);

        Integer objectType = messageDeleteRequest.getObjectType();
        Long objectId = messageDeleteRequest.getObjectId();
        Long commentId = messageDeleteRequest.getCommentId();

        //查出所有要删除的消息
        List<MessageInfoModel> messageList = messageInfoService.lambdaQuery()
                .select(MessageInfoModel::getMessageId)
                .eq(objectType != null && objectType != 0, MessageInfoModel::getObjectType, objectType)
                .eq(MessageInfoModel::getObjectId, objectId)
                .eq(commentId != null && commentId != 0, MessageInfoModel::getCommentId, commentId)
                .list();
        if (CollectionUtil.isEmpty(messageList)){
            // 没有要删的消息 直接return
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            return;
        }
        // 要删除的消息id
        List<Long> messageIds = messageList.stream().map(MessageInfoModel::getMessageId).collect(Collectors.toList());

        //未读消息提醒列表
        List<MessageNoticeModel> messageNoticeList = messageNoticeService.lambdaQuery()
                .eq(MessageNoticeModel::getReadFlag, false)
                .in(MessageNoticeModel::getMessageId, messageIds).list();

        //删除所有消息提醒
        messageNoticeService.lambdaUpdate()
                .in(MessageNoticeModel::getMessageId, messageIds)
                .set(MessageNoticeModel::getDelFlag, true)
                .update();

        //删除所有消息信息
        messageInfoService.lambdaUpdate()
                .in(MessageInfoModel::getMessageId, messageIds)
                .set(MessageInfoModel::getDelFlag, true)
                .update();

        //更新缓存未读消息数量
        for (MessageNoticeModel noticeModel : messageNoticeList) {
            Long receiveId = noticeModel.getReceiveId();
            Integer messageType = noticeModel.getMessageType();
            if (messageType.equals(MessageTypeEnum.REPLY.getMessageType())) {
                redisService.decr(CacheConstants.MESSAGE_NOT_READ_COUNT_REPLY + receiveId);
            } else if (messageType.equals(MessageTypeEnum.LIKE.getMessageType())) {
                redisService.decr(CacheConstants.MESSAGE_NOT_READ_COUNT_LIKE + receiveId);
            } else if (messageType.equals(MessageTypeEnum.FOLLOW.getMessageType())) {
                redisService.decr(CacheConstants.MESSAGE_NOT_READ_COUNT_FOLLOW + receiveId);
            } else if (messageType.equals(MessageTypeEnum.SYSTEM.getMessageType())) {
                redisService.decr(CacheConstants.MESSAGE_NOT_READ_COUNT_SYSTEM + receiveId);
            }
            redisService.decr(CacheConstants.MESSAGE_NOT_READ_COUNT_ALL + receiveId);
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

    }

}
