package com.zdk.mai.common.security.annotation;

import java.lang.annotation.*;

/**
 * @Description 初始化请求参数注解
 * @Author zdk
 * @Date 2023/2/28 20:47
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InitRequestArg {

    /**
     * 操作类型
     * @return
     */
    ArgType argType();

}
