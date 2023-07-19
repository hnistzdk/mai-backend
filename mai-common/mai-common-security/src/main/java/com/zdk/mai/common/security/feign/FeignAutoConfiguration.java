package com.zdk.mai.common.security.feign;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description feign配置
 * @Author zdk
 * @Date 2023/3/7 18:10
 */
@Configuration
public class FeignAutoConfiguration {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new FeignRequestInterceptor();
    }
}
