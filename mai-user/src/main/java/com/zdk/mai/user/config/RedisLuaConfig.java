package com.zdk.mai.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/22 21:21
 */
@Configuration
public class RedisLuaConfig {
    /**
     * 注入lua脚本
     * @return
     */
    @Bean
    public DefaultRedisScript<Void> userFollowUpdateScript(){
        DefaultRedisScript<Void> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("user_follow_update.lua"));
        return script;
    }

}
