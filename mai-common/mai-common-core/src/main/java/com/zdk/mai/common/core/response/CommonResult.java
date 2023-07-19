package com.zdk.mai.common.core.response;

import com.zdk.mai.common.core.constant.CommonConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @Description
 * @Author zdk
 * @Date 2022/11/27 14:55
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
public class CommonResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int SUCCESS = CommonConstants.SUCCESS;

    public static final int FAIL = CommonConstants.FAIL;

    public static final String MSG_SUCCESS = "成功";

    public static final String MSG_FAIL = "失败";

    private int code;

    private T data;

    private String msg;

    /**
     * 扩展参数map
     */
    private HashMap<String,Object> extData;

    public CommonResult() {
        this.code = SUCCESS;
        this.msg = MSG_SUCCESS;
    }

    public CommonResult(int code) {
        this.code = code;
        this.msg = MSG_SUCCESS;
    }

    public CommonResult(int code, T data) {
        this.code = code;
        this.data = data;
        this.msg = MSG_SUCCESS;
    }

    public CommonResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public CommonResult(T data, String msg) {
        this.code = SUCCESS;
        this.msg = msg;
        this.data = data;
    }

    public CommonResult(int code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    public static <T> CommonResult<T> success() {
        return new CommonResult<>();
    }

    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<>(SUCCESS, data, MSG_SUCCESS);
    }

    public static <T> CommonResult<T> success(String msg) {
        return new CommonResult<>(SUCCESS, msg);
    }

    public static <T> CommonResult<T> success(T data, String msg) {
        return new CommonResult<>(data, msg);
    }

    public static <T> CommonResult<T> fail() {
        return new CommonResult<>(FAIL, MSG_FAIL);
    }

    public static <T> CommonResult<T> fail(String msg) {
        return new CommonResult<>(FAIL, msg);
    }

    public static <T> CommonResult<T> fail(int errorCode) {
        return new CommonResult<>(errorCode);
    }

    public static <T> CommonResult<T> fail(int errorCode,String msg) {
        return new CommonResult<>(errorCode,msg);
    }

    public static <T> CommonResult<T> getResult(boolean res) {
        return res ? success() : fail();
    }

    public static <T> CommonResult<T> result(T data, int code, String msg) {
        CommonResult<T> apiResult = new CommonResult<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        return apiResult;
    }

    /**
     * 是否成功
     *
     * @return
     */
    public static <T> Boolean isSuccess(CommonResult<T> result) {
        return CommonResult.SUCCESS == result.getCode();
    }

    /**
     * 是否失败
     *
     * @return
     */
    public static <T> Boolean isError(CommonResult<T> result) {
        return !isSuccess(result);
    }


    public CommonResult put(String key, Object value) {
        if (extData == null){
            extData = new HashMap<>();
        }
        extData.put(key, value);
        return this;
    }
}
