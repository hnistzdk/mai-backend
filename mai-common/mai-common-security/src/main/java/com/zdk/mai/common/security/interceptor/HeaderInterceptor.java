package com.zdk.mai.common.security.interceptor;

import com.zdk.mai.common.core.constant.SecurityConstants;
import com.zdk.mai.common.core.context.SecurityContextHolder;
import com.zdk.mai.common.core.utils.ServletUtils;
import com.zdk.mai.common.model.user.vo.LoginUserVO;
import com.zdk.mai.common.redis.service.RedisService;
import com.zdk.mai.common.security.annotation.NotRequiresLogin;
import com.zdk.mai.common.security.annotation.Uncheck;
import com.zdk.mai.common.security.service.TokenService;
import com.zdk.mai.common.security.utils.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description 自定义请求头拦截器，将Header数据封装到线程变量中方便获取
 * 注意：此拦截器会同时验证当前用户有效期自动刷新有效期
 * @Author zdk
 * @Date 2023/2/26 20:17
 *
 */
public class HeaderInterceptor implements AsyncHandlerInterceptor {

    private TokenService tokenService;

    private RedisService redisService;

    public void setTokenService(TokenService tokenService,RedisService redisService) {
        this.tokenService = tokenService;
        this.redisService = redisService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }


        //设置header中携带的用户主要信息到当前线程
        SecurityContextHolder.setUserId(ServletUtils.getHeader(request, SecurityConstants.DETAILS_USER_ID));
        SecurityContextHolder.setUserName(ServletUtils.getHeader(request, SecurityConstants.DETAILS_USERNAME));
        SecurityContextHolder.setUserKey(ServletUtils.getHeader(request, SecurityConstants.USER_KEY));
        //不需要检查的
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Class<?> beanType = handlerMethod.getBeanType();
        if (beanType.getAnnotation(Uncheck.class) != null){
            return true;
        }
        if (handlerMethod.getMethodAnnotation(Uncheck.class) != null) {
            return true;
        }
        if (handlerMethod.getMethodAnnotation(NotRequiresLogin.class) != null) {
            return true;
        }

        String token = SecurityUtils.getToken();
        if (StringUtils.isNotEmpty(token)) {
            LoginUserVO loginUser = tokenService.getLoginUser(token);
            if (loginUser != null) {
                //校验token有效期 间隔120分钟内自动刷新
                tokenService.verifyToken(loginUser);
                //存入用户信息到当前线程
                SecurityContextHolder.set(SecurityConstants.LOGIN_USER, loginUser);
            }
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        SecurityContextHolder.remove();
    }
}
