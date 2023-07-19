package com.zdk.mai.common.core.exception;

/**
 * @Description
 * @Author zdk
 * @Date 2022/11/30 17:24
 */
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private Integer errorCode;

    public BusinessException() {
    }

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(Integer errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }

    public Integer getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }
}
