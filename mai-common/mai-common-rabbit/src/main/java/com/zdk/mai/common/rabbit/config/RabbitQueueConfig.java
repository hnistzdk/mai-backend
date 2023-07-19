package com.zdk.mai.common.rabbit.config;

import com.zdk.mai.common.rabbit.constant.RabbitConstants;
import org.springframework.amqp.core.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description RabbitMQ队列配置
 * @Author zdk
 * @Date 2023/3/21 19:32
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.rabbitmq", name = "host")
public class RabbitQueueConfig {


    /**
     * 更新MySQL队列 user
     * @return
     */
    @Bean
    public Queue updateUserDbQueue(){
        return new Queue(RabbitConstants.SYNC_DB_QUEUE_USER,true);
    }

    /**
     * 更新MySQL队列 post
     * @return
     */
    @Bean
    public Queue updatePostDbQueue(){
        return new Queue(RabbitConstants.SYNC_DB_QUEUE_POST,true);
    }

    /**
     * 更新MySQL队列 comment
     * @return
     */
    @Bean
    public Queue updateCommentDbQueue(){
        return new Queue(RabbitConstants.SYNC_DB_QUEUE_COMMENT,true);
    }


    /**
     * 更新ES队列 用户
     * @return
     */
    @Bean
    public Queue updateUserEsQueue(){
        return new Queue(RabbitConstants.SYNC_ES_QUEUE_USER,true);
    }

    /**
     * 更新ES队列 贴子
     * @return
     */
    @Bean
    public Queue updatePostEsQueue(){
        return new Queue(RabbitConstants.SYNC_ES_QUEUE_POST,true);
    }

    /**
     * 更新ES队列 评论
     * @return
     */
    @Bean
    public Queue updateCommentEsQueue(){
        return new Queue(RabbitConstants.SYNC_ES_QUEUE_COMMENT,true);
    }

    /**
     * 消息通知队列
     * @return
     */
    @Bean
    public Queue messageDbQueue(){
        return new Queue(RabbitConstants.MESSAGE_DB_QUEUE,true);
    }


    /**
     * 同步ES数据交换机
     * @return
     */
    @Bean
    public TopicExchange syncDataExchange(){
        return new TopicExchange(RabbitConstants.SYNC_DATA_EXCHANGE);
    }

    /**
     * Db更新队列绑定到数据同步交换机 user
     * @return
     */
    @Bean
    public Binding syncUserDbBinding(){
        return BindingBuilder.bind(updateUserDbQueue()).to(syncDataExchange()).with(RabbitConstants.SYNC_DB_ROUTING_KEY_USER);
    }

    /**
     * Db更新队列绑定到数据同步交换机 post
     * @return
     */
    @Bean
    public Binding syncPostDbBinding(){
        return BindingBuilder.bind(updatePostDbQueue()).to(syncDataExchange()).with(RabbitConstants.SYNC_DB_ROUTING_KEY_POST);
    }

    /**
     * Db更新队列绑定到数据同步交换机 comment
     * @return
     */
    @Bean
    public Binding syncCommentDbBinding(){
        return BindingBuilder.bind(updateCommentDbQueue()).to(syncDataExchange()).with(RabbitConstants.SYNC_DB_ROUTING_KEY_COMMENT);
    }

    /**
     * Es更新队列绑定到数据同步交换机
     * @return
     */
    @Bean
    public Binding syncUserEsBinding(){
        return BindingBuilder.bind(updateUserEsQueue()).to(syncDataExchange()).with(RabbitConstants.SYNC_ES_ROUTING_KEY_USER);
    }

    /**
     * Es更新队列绑定到数据同步交换机
     * @return
     */
    @Bean
    public Binding syncPostEsBinding(){
        return BindingBuilder.bind(updatePostEsQueue()).to(syncDataExchange()).with(RabbitConstants.SYNC_ES_ROUTING_KEY_POST);
    }

    /**
     * Es更新队列绑定到数据同步交换机
     * @return
     */
    @Bean
    public Binding syncCommentEsBinding(){
        return BindingBuilder.bind(updateCommentEsQueue()).to(syncDataExchange()).with(RabbitConstants.SYNC_ES_ROUTING_KEY_COMMENT);
    }

    /**
     * 绑定
     * @return
     */
    @Bean
    public Binding messageDbBinding(){
        return BindingBuilder.bind(messageDbQueue()).to(syncDataExchange()).with(RabbitConstants.MESSAGE_DB_ROUTING_KEY);
    }




}
