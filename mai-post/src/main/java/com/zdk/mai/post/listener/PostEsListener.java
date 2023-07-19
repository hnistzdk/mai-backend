package com.zdk.mai.post.listener;

import com.rabbitmq.client.Channel;
import com.zdk.mai.api.user.RemoteUserService;
import com.zdk.mai.common.core.constant.SecurityConstants;
import com.zdk.mai.common.es.mapper.EsPostMapper;
import com.zdk.mai.common.es.model.EsPostModel;
import com.zdk.mai.common.model.post.PostModel;
import com.zdk.mai.common.model.post.request.PostDeleteRequest;
import com.zdk.mai.common.rabbit.constant.RabbitConstants;
import com.zdk.mai.common.redis.service.RedisService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/21 20:23
 */

@Slf4j
@Component
@RabbitListener(queues = {RabbitConstants.SYNC_ES_QUEUE_POST})
public class PostEsListener {

    @Autowired
    private RedisService redisService;

    @Resource
    private EsPostMapper esPostMapper;

    @Autowired
    private RemoteUserService remoteUserService;


    /**
     * 同步数据
     * @param postModel
     * @param channel
     * @param message
     */
    @SneakyThrows
    @RabbitHandler
    public void syncPostToEs(PostModel postModel, Channel channel, Message message){


        log.info("收到更新贴子信息到es消息 id->{}",postModel.getPostId());

        EsPostModel esModel = new EsPostModel();

        BeanUtils.copyProperties(postModel, esModel);

        String avatar = redisService.getAvatar(postModel.getAuthorId());
        if (avatar == null){
            avatar = remoteUserService.getAvatar(postModel.getAuthorId(), SecurityConstants.INNER);
        }
        esModel.setAvatar(avatar);

        esPostMapper.insert(esModel);

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

    }

    /**
     * 删除es文章
     * @param deleteRequest
     * @param channel
     * @param message
     */
    @SneakyThrows
    @RabbitHandler
    public void syncPostToEs(PostDeleteRequest deleteRequest, Channel channel, Message message){

        log.info("收到删除搜索引擎数据消息：" + deleteRequest.getPostId());

        esPostMapper.deleteById(deleteRequest.getPostId());

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
