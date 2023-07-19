package com.zdk.mai.common.redis.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @Description jedis连接池配置
 * @Author zdk
 * @Date 2023/3/19 17:00
 */
@Slf4j
@Configuration
public class JedisConfig {

    @Autowired
    private RedisProperties redisProperties;

    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        RedisProperties.Pool pool = redisProperties.getJedis().getPool();
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(pool.getMaxActive());
        jedisPoolConfig.setMaxIdle(pool.getMaxIdle());
        jedisPoolConfig.setMinIdle(pool.getMinIdle());
        jedisPoolConfig.setMaxWait(pool.getMaxWait());
        return jedisPoolConfig;
    }

    @Bean
    public RedisStandaloneConfiguration jedisConfig() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisProperties.getHost());
        config.setPort(redisProperties.getPort());
        config.setDatabase(redisProperties.getDatabase());
        config.setPassword(RedisPassword.of(redisProperties.getPassword()));
        return config;
    }


}
