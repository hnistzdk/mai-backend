package com.zdk.mai.common.security.config;

import com.zdk.mai.common.redis.service.RedisService;
import com.zdk.mai.common.security.interceptor.HeaderInterceptor;
import com.zdk.mai.common.security.interceptor.MessageCountInterceptor;
import com.zdk.mai.common.security.interceptor.UserAvatarInterceptor;
import com.zdk.mai.common.security.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Description 拦截器配置
 * @Author zdk
 * @Date 2023/2/26 21:24
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private RedisService redisService;

    /**
     * 不需要拦截的url
     */
    public static final String[] EXCLUDE_URLS = {"/auth/login", "/auth/logout", "/auth/refresh"};

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getHeaderInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(EXCLUDE_URLS)
                .order(-10);
        registry.addInterceptor(getUserAvatarInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(EXCLUDE_URLS)
                .order(-9);
        registry.addInterceptor(getMessageCountInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(EXCLUDE_URLS)
                .order(-8);
    }

    /**
     * 自定义请求头拦截器
     */
    public HeaderInterceptor getHeaderInterceptor() {
        HeaderInterceptor headerInterceptor = new HeaderInterceptor();
        headerInterceptor.setTokenService(tokenService,redisService);
        return headerInterceptor;
    }

    public UserAvatarInterceptor getUserAvatarInterceptor(){
        UserAvatarInterceptor userAvatarInterceptor = new UserAvatarInterceptor();
        userAvatarInterceptor.setTokenService(tokenService,redisService);
        return userAvatarInterceptor;
    }

    public MessageCountInterceptor getMessageCountInterceptor(){
        MessageCountInterceptor messageCountInterceptor = new MessageCountInterceptor();
        messageCountInterceptor.setTokenService(tokenService,redisService);
        return messageCountInterceptor;
    }
}
