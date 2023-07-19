package com.zdk.mai.common.log.annotation;

import com.zdk.mai.common.log.enums.BusinessType;

import java.lang.annotation.*;

/**
 * @Description 自定义操作日志记录注解
 * @Author zdk
 * @Date 2023/2/26 18:33
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AccessLog {
    /**
     * 模块
     */
    String title() default "";

    /**
     * 功能
     */
    BusinessType businessType() default BusinessType.OTHER;

    /**
     * 是否保存请求的参数
     */
    boolean isSaveRequestData() default true;

    /**
     * 是否保存响应的参数
     */
    boolean isSaveResponseData() default true;
}
