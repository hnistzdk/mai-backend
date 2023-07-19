package com.zdk.mai.common.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;

/**
 * @Description 配置lua注入
 * @Author zdk
 * @Date 2023/3/13 22:10
 */
@Configuration
public class RedisLuaConfig {

    @Bean
    public DefaultRedisScript<Long> rateLimiterScript(){
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setResultType(Long.class);
        script.setLocation(new ClassPathResource("rateLimiter.lua"));
        return script;
    }

}
