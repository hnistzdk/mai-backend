package com.zdk.mai.common.security.annotation;

/**
 * @Description
 * @Author zdk
 * @Date 2023/2/26 22:05
 */
public enum Logical {
    /**
     * 必须具有所有的元素
     */
    AND,

    /**
     * 只需具有其中一个元素
     */
    OR
}
