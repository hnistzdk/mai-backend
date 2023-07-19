package com.zdk.mai.common.security.interceptor;

import com.zdk.mai.common.core.constant.CacheConstants;
import com.zdk.mai.common.core.constant.HeaderConstants;
import com.zdk.mai.common.core.context.SecurityContextHolder;
import com.zdk.mai.common.model.user.vo.LoginUserVO;
import com.zdk.mai.common.redis.service.RedisService;
import com.zdk.mai.common.security.service.TokenService;
import com.zdk.mai.common.security.utils.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description
 * @Author zdk
 * @Date 2023/4/3 16:15
 */
public class MessageCountInterceptor implements AsyncHandlerInterceptor {

    private TokenService tokenService;

    private RedisService redisService;

    public void setTokenService(TokenService tokenService,RedisService redisService) {
        this.tokenService = tokenService;
        this.redisService = redisService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = SecurityUtils.getToken(request);
        if (StringUtils.isEmpty(token)){
            return true;
        }
        LoginUserVO loginUser = tokenService.getLoginUser(token);
        if (loginUser == null || loginUser.getUserId() == null){
            return true;
        }
        Long userId = loginUser.getUserId();
        Integer replyCount = redisService.getCacheObject(CacheConstants.MESSAGE_NOT_READ_COUNT_REPLY + userId);
        Integer likeCount = redisService.getCacheObject(CacheConstants.MESSAGE_NOT_READ_COUNT_LIKE + userId);
        Integer followCount = redisService.getCacheObject(CacheConstants.MESSAGE_NOT_READ_COUNT_FOLLOW + userId);
        Integer systemCount = redisService.getCacheObject(CacheConstants.MESSAGE_NOT_READ_COUNT_SYSTEM + userId);
        replyCount = replyCount == null ? 0 : replyCount;
        likeCount = likeCount == null ? 0 : likeCount;
        followCount = followCount == null ? 0 : followCount;
        systemCount = systemCount == null ? 0 : systemCount;
        response.setHeader(HeaderConstants.MESSAGE_REPLY_COUNT_HEADER, String.valueOf(replyCount));
        response.setHeader(HeaderConstants.MESSAGE_LIKE_COUNT_HEADER, String.valueOf(likeCount));
        response.setHeader(HeaderConstants.MESSAGE_FOLLOW_COUNT_HEADER, String.valueOf(followCount));
        response.setHeader(HeaderConstants.MESSAGE_SYSTEM_COUNT_HEADER, String.valueOf(systemCount));
        response.setHeader(HeaderConstants.MESSAGE_ALL_COUNT_HEADER, String.valueOf(replyCount+likeCount+followCount+systemCount));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        SecurityContextHolder.remove();
    }

}
