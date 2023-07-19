package com.zdk.mai.common.redis.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @Description Redisson配置类
 * @Author zdk
 * @Date 2023/3/21 20:02
 */
@Configuration
public class RedissonConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private String port;

    @Value("${spring.redis.password}")
    private String redisPassword;

    @Primary
    @Bean
    public RedissonClient getRedisson(){

        Config config = new Config();
        //单机模式
        config.useSingleServer()
                .setAddress("redis://" + host + ":" + port)
                .setPassword(redisPassword)
                .setDatabase(0);
        return Redisson.create(config);
    }
}
