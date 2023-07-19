package com.zdk.mai.common.security.aspect;

import com.zdk.mai.common.core.exception.RateLimitException;
import com.zdk.mai.common.core.utils.IpUtils;
import com.zdk.mai.common.redis.service.RedisService;
import com.zdk.mai.common.security.annotation.RateLimiter;
import com.zdk.mai.common.security.enums.ErrorEnum;
import com.zdk.mai.common.security.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Description 服务内部接口限流切面
 * @Author zdk
 * @Date 2023/3/12 21:35
 */
@Slf4j
@Aspect
@Component
public class RateLimiterAspect {


    private final ExpressionParser parser = new SpelExpressionParser();

    @Autowired
    private RedisService redisService;

    @Resource
    private DefaultRedisScript<Long> rateLimiterScript;


    public static final String SEPARATOR = ":";


    public static final String SPEL_PREFIX = "#";


    @Around("@annotation(rateLimiter)")
    public Object innerAround(ProceedingJoinPoint point, RateLimiter rateLimiter) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();


        String[] paramNames = signature.getParameterNames();
        Stream<?> stream = ArrayUtils.isEmpty(point.getArgs()) ? Stream.empty() : Arrays.stream(point.getArgs());
        List<Object> paramValues = stream
                .filter(arg -> (!(arg instanceof HttpServletRequest) && !(arg instanceof HttpServletResponse)))
                .collect(Collectors.toList());
        String prefix = rateLimiter.prefix();
        String rateKey = rateLimiter.key();
        int expireTime = rateLimiter.expireTime();
        int maxCount = rateLimiter.maxCount();
        String rateMessage = rateLimiter.rateMessage();
        boolean limitIp = rateLimiter.limitIp();

        log.info("原始rateKey ->{}", rateKey);

        rateKey = buildLimitKey(prefix, rateKey, limitIp, method.getName(), paramNames, paramValues);

        log.info("构造后rateKey ->{}", rateKey);

        Long current = redisService.execute(rateLimiterScript, Collections.singletonList(rateKey), expireTime, maxCount);
        if (current == null || current.intValue() > maxCount) {
            log.error("当前接口达到最大限流次数");
            throw new RateLimitException(ErrorEnum.RATELIMIT.getCode(), StringUtils.isEmpty(rateMessage) ? ErrorEnum.RATELIMIT.getMsg() : rateMessage);
        }

        log.info("{}秒内允许的最大请求次数->{},当前请求次数->{},缓存的rateKey为->{}", expireTime, maxCount, current, rateKey);

        return point.proceed();
    }

    /**
     * 构造缓存key<br>
     * 1.spel情况: prefix:方法名:ip:key:spelValue<br>
     * 2.非spel:   prefix:方法名:ip
     * 3.在两种情况的最后 看是否存在登录人id 存在就再拼上一个:userId:(userId)
     * @param prefix 前缀 服务的类别:user、post、comment等
     * @param rateKey 一般为spel表达式,非的时候不使用此参数,直接用方法名代替
     * @param methodName 方法名
     * @param paramNames 参数名列表
     * @param paramValues 参数值列表
     * @return
     */
    public String buildLimitKey(String prefix, String rateKey,boolean limitIp, String methodName, String[] paramNames, List<Object> paramValues){
        String ip = IpUtils.getHostIp();
        Long userId = SecurityUtils.getUserId();
        StringBuilder builder = new StringBuilder();
        if (!rateKey.startsWith(SPEL_PREFIX)) {
            //不是spel表达式
            builder.append(prefix).append(SEPARATOR).append(methodName);
        }else {
            String parseValue = parseSpel(paramNames, paramValues, rateKey, String.class);
            builder.append(prefix).append(SEPARATOR)
                    .append(methodName).append(SEPARATOR)
                    .append(rateKey).append(SEPARATOR)
                    .append(parseValue);
        }
        if (limitIp){
            builder.append(SEPARATOR).append(ip);
        }
        if (userId != null && userId != 0){
            builder.append(SEPARATOR).append("userId").append(SEPARATOR).append(userId);
        }
        return builder.toString();
    }


    /**
     * 解析 spel 表达式
     *
     * @param paramNames 参数名
     * @param paramValues  参数值
     * @param spel       表达式
     * @param clazz      返回结果的类型
     * @return 执行spel表达式后的结果
     */
    private <T> T parseSpel(String[] paramNames, List<Object> paramValues, String spel, Class<T> clazz) {
        EvaluationContext context = new StandardEvaluationContext();
        for (int len = 0; len < Objects.requireNonNull(paramNames).length; len++) {
            context.setVariable(paramNames[len], paramValues.get(len));
        }
        Expression expression = parser.parseExpression(spel);
        return expression.getValue(context, clazz);
    }


}
