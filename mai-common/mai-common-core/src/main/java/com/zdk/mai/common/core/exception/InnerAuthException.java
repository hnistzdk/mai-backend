package com.zdk.mai.common.core.exception;

/**
 * @Description
 * @Author zdk
 * @Date 2023/2/26 21:58
 */
public class InnerAuthException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InnerAuthException(String message) {
        super(message);
    }
}
