package com.zdk.mai.common.rabbit.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * @Description 消息投递到交换机和队列的失败回调
 * @Author zdk
 * @Date 2023/3/25 21:49
 */
@Slf4j
@Component
public class MessageCallback implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnsCallback {

    /**
     * 消息发送到交换机的回调
     * @param correlationData 消息相关数据
     * @param ack 消息是否被交换机确认了
     * @param cause 没被确认的原因
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        String id = correlationData != null ? correlationData.getId() : null;
        if (ack) {
            log.info("交换机已收到 id 为:{}的消息",id);
        }else{
            log.info("交换机未收到 id 为:{}的消息,原因是:{}",id,cause);
        }
    }

    /**
     * 消息未投递到队列的回调
     * @param returned 投递失败的消息和元数据
     */
    @Override
    public void returnedMessage(ReturnedMessage returned) {
        log.error("消息：{}，被交换机 {} 退回，原因：{}，路由key：{},code:{}",
                new String(returned.getMessage().getBody()), returned.getExchange(),
                returned.getReplyText(), returned.getRoutingKey(),
                returned.getReplyCode());
    }

}
