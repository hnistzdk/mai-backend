package com.zdk.mai.common.core.exception;

/**
 * @Description 内部接口限流异常类
 * @Author zdk
 * @Date 2023/3/13 22:13
 */
public class RateLimitException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private Integer code;

    private String msg;

    public RateLimitException(String msg) {
        super(msg);
    }

    public RateLimitException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
