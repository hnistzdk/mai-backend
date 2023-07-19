package com.zdk.mai.comment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/7 14:03
 */
@Configuration
public class RedisLuaConfig {

    /**
     * 注入lua脚本
     * @return
     */
    @Bean
    public DefaultRedisScript<Void> commentLikeUpdateScript(){
        DefaultRedisScript<Void> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("comment_like_update.lua"));
        return script;
    }


}
