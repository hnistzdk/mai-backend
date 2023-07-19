package com.zdk.mai.comment.listener;

import cn.easyes.core.conditions.LambdaEsQueryWrapper;
import com.rabbitmq.client.Channel;
import com.zdk.mai.common.es.mapper.EsCommentMapper;
import com.zdk.mai.common.es.model.EsCommentModel;
import com.zdk.mai.common.model.comment.CommentModel;
import com.zdk.mai.common.model.comment.request.CommentDeleteRequest;
import com.zdk.mai.common.rabbit.constant.RabbitConstants;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description 评论es同步消费者
 * @Author zdk
 * @Date 2023/3/23 14:47
 */
@Slf4j
@Component
@RabbitListener(queues = {RabbitConstants.SYNC_ES_QUEUE_COMMENT})
public class CommentEsListener {

    @Resource
    private EsCommentMapper esCommentMapper;


    /**
     * 同步数据
     * @param model
     * @param channel
     * @param message
     */
    @SneakyThrows
    @RabbitHandler
    public void syncCommentToEs(CommentModel model, Channel channel, Message message){

        log.info("收到评论同步搜索引擎消息->{}", model.getCommentId());

        EsCommentModel esModel = new EsCommentModel();

        BeanUtils.copyProperties(model, esModel);

        esCommentMapper.insert(esModel);

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

    }

    /**
     * 删除es评论
     * @param commentDeleteRequest
     * @param channel
     * @param message
     */
    @SneakyThrows
    @RabbitHandler
    public void deleteCommentFromEs(CommentDeleteRequest commentDeleteRequest, Channel channel, Message message){

        log.info("收到删除es评论信息消息->{}", commentDeleteRequest);
        Long commentId = commentDeleteRequest.getCommentId();
        LambdaEsQueryWrapper<EsCommentModel> queryWrapper = new LambdaEsQueryWrapper<>();
        queryWrapper.eq(EsCommentModel::getCommentId, commentId)
                        .or()
                        .eq(EsCommentModel::getParentId, commentId)
                        .or()
                        .eq(EsCommentModel::getRootId, commentId);

        Integer delete = esCommentMapper.delete(queryWrapper);

        log.info("删除es评论条数->{}", delete);

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

    }

}
