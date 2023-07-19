package com.zdk.mai.common.rabbit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


/**
 * @Description RabbitMQ序列化配置
 * @Author zdk
 * @Date 2023/3/22 11:26
 */
@Configuration
public class RabbitConfig {

    @Autowired
    private MessageCallback messageCallback;



    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }


    /**
     * json序列化
     * @param connectionFactory
     * @return
     */
    @Primary
    @Bean
    public RabbitTemplate jacksonRabbitTemplate(RabbitTemplateConfigurer configurer, ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitTemplate.setConfirmCallback(messageCallback);
        /*
          true：交换机无法将消息进行路由时，会将该消息返回给生产者
          false：如果发现消息无法进行路由，则直接丢弃
         */
        rabbitTemplate.setMandatory(true);
        //设置回退消息交给谁处理
        rabbitTemplate.setReturnsCallback(messageCallback);
        configurer.configure(rabbitTemplate, connectionFactory);
        return rabbitTemplate;
    }

}
