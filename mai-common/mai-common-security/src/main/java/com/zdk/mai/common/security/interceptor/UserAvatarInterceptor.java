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
 * @Description 头像放入header返回
 * @Author zdk
 * @Date 2023/3/27 16:32
 */
public class UserAvatarInterceptor implements AsyncHandlerInterceptor {

    private TokenService tokenService;

    private RedisService redisService;

    public void setTokenService(TokenService tokenService,RedisService redisService) {
        this.tokenService = tokenService;
        this.redisService = redisService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = SecurityUtils.getToken();
        if (StringUtils.isEmpty(token)){
            return true;
        }
        LoginUserVO loginUser = tokenService.getLoginUser(token);

        if (loginUser == null){
            return true;
        }

        //头像设置回去
        String avatar = redisService.getCacheObject(CacheConstants.USER_AVATAR_KEY + loginUser.getUserId());
        if (StringUtils.isEmpty(avatar) && loginUser.getUserModel() != null){
            avatar = loginUser.getUserModel().getAvatar();
        }
        response.setHeader(HeaderConstants.USER_AVATAR_HEADER, avatar);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        SecurityContextHolder.remove();
    }

}
