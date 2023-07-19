package com.zdk.main.gateway.filter;

import com.zdk.mai.common.core.constant.CacheConstants;
import com.zdk.mai.common.core.constant.SecurityConstants;
import com.zdk.mai.common.core.utils.JwtUtil;
import com.zdk.mai.common.core.utils.ServletUtils;
import com.zdk.mai.common.redis.service.RedisService;
import com.zdk.main.gateway.config.properties.IgnoreWhiteProperties;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @Description 网关鉴权
 * @Author zdk
 * @Date 2022/11/30 16:34
 */
@Slf4j
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    @Autowired
    private RedisService redisService;

    @Autowired
    private IgnoreWhiteProperties ignoreWhiteProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpRequest.Builder mutate = request.mutate();
        String url = request.getURI().getPath();
        // 跳过不需要验证的路径
        if (matches(url, ignoreWhiteProperties.getWhites())) {
            return chain.filter(exchange);
        }
        //令牌有无
        String token = getToken(request);
        if (StringUtils.isEmpty(token)) {
            return unauthorizedResponse(exchange, "令牌不能为空,请先登录");
        }
        //验证令牌
        Claims claims = JwtUtil.parseToken(token);
        if (claims == null) {
            return unauthorizedResponse(exchange, "令牌已过期或验证错误");
        }
        String userKey = JwtUtil.getUserKey(token);
        //1.通过随机token+前缀获取用户信息缓存是否过期
        boolean isLogin = redisService.hasKey(CacheConstants.LOGIN_TOKEN_KEY + userKey);
        if (!isLogin) {
            return unauthorizedResponse(exchange, "登录已过期");
        }
        //2.根据令牌获取信息
        String userId = JwtUtil.getUserId(claims);
        String username = JwtUtil.getUserName(claims);
        //如果有信息是空
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(username)) {
            return unauthorizedResponse(exchange, "令牌验证失败");
        }
        //以上进行redis和jwt双重验证
        // 设置用户信息到请求
        addHeader(mutate, SecurityConstants.USER_KEY, userKey);
        addHeader(mutate, SecurityConstants.DETAILS_USER_ID, userId);
        addHeader(mutate, SecurityConstants.DETAILS_USERNAME, username);
        // 内部请求来源参数清除
        removeHeader(mutate, SecurityConstants.FROM_SOURCE);
        return chain.filter(exchange.mutate().request(mutate.build()).build());
    }

    /**
     * 获取token
     *
     * @param request
     * @return
     */
    private String getToken(ServerHttpRequest request) {
        return request.getHeaders().getFirst(SecurityConstants.AUTHORIZATION_HEADER);
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange,

                                            String msg) {
        log.error("[鉴权异常处理]请求路径:{}", exchange.getRequest().getPath());
        return ServletUtils.webFluxResponseWriter(exchange.getResponse(), msg, HttpStatus.UNAUTHORIZED.value());
    }

    /**
     * 查找指定字符串是否匹配指定字符串列表中的任意一个字符串
     *
     * @param str  指定字符串
     * @param strs 需要检查的字符串数组
     * @return 是否匹配
     */
    private boolean matches(String str, List<String> strs) {
        if (StringUtils.isEmpty(str) || strs.isEmpty()) {
            return false;
        }
        AntPathMatcher matcher = new AntPathMatcher();
        for (String pattern : strs) {
            if (isMatch(pattern, str, matcher)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 判断url是否与规则配置:
     * ? 表示单个字符;
     * * 表示一层路径内的任意字符串，不可跨层级;
     * ** 表示任意层路径;
     *
     * @param pattern 匹配规则
     * @param url     需要匹配的url
     * @param matcher 匹配器
     * @return
     */
    private boolean isMatch(String pattern, String url, AntPathMatcher matcher) {
        return matcher.match(pattern, url);
    }


    private void addHeader(ServerHttpRequest.Builder mutate, String name, Object value) {
        if (value == null) {
            return;
        }
        String valueStr = value.toString();
        String valueEncode = ServletUtils.urlEncode(valueStr);
        mutate.header(name, valueEncode);
    }

    private void removeHeader(ServerHttpRequest.Builder mutate, String name) {
        mutate.headers(httpHeaders -> httpHeaders.remove(name)).build();
    }

    @Override
    public int getOrder() {
        return -200;
    }
}
