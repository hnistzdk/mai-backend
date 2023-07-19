package com.zdk.mai.common.security.annotation;

import java.lang.annotation.*;

/**
 * @Description 内部接口限流注解
 * @Author zdk
 * @Date 2023/3/12 21:30
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RateLimiter {

    /**
     * Key的prefix
     *
     * @return String
     */
    String prefix() default "";


    /**
     * 资源的key
     *
     * @return String
     */
    String key() default "";


    /**
     * 给定的过期时间
     * 单位秒 默认60s
     *
     * @return int
     */
    int expireTime() default 60;


    /**
     * 最大的访问限制次数
     *
     * @return int
     */
    int maxCount() default 50;

    /**
     * 是否限制ip(为false的话则与用户唯一绑定 换ip也没用)
     * @return boolean
     */
    boolean limitIp() default true;

    String rateMessage() default "";
}
