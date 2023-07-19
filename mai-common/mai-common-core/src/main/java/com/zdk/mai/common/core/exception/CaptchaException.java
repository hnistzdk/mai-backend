package com.zdk.mai.common.core.exception;

/**
 * @Description
 * @Author zdk
 * @Date 2022/11/30 15:51
 */
public class CaptchaException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public CaptchaException(String msg)
    {
        super(msg);
    }
}
