package com.zdk.mai.common.security.annotation;

import java.lang.annotation.*;

/**
 * @Description 内部认证注解 用于系统内部调用时不需用户认证
 * @Author zdk
 * @Date 2023/2/26 21:56
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InnerAuth {
    /**
     * 是否校验用户信息
     */
    boolean isUser() default false;
}
