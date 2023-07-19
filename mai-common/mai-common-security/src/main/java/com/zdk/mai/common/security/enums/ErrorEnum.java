package com.zdk.mai.common.security.enums;

/**
 * @Description 错误枚举: code+msg
 * @Author zdk
 * @Date 2022/12/4 20:06
 */
@SuppressWarnings("all")
public enum ErrorEnum {
    OK(200, "OK"),
    HTTP_ERROR_100(100, "1XX错误"),
    HTTP_ERROR_300(300, "3XX错误"),
    HTTP_ERROR_400(400, "4XX错误"),
    HTTP_ERROR_500(500, "5XX错误"),
    REQUEST_NOT_FOUND(404, "访问地址不存在！"),
    METHOD_NOT_ALLOWED(405, "不支持当前请求方法"),
    BAD_REQUEST( 407, "参数解析失败"),
    BUSINESS_ERROR(10000, "期待值为空找不到想要的对象"),
    SYSTEM_ERROR(10001, "系统运行时异常"),
    UNIQUENESS_ERROR(10002, "唯一性错误，出现了不允许重复的内容"),
    EXPECTATION_NULL(10003, "期待值为空找不到想要的对象"),
    INVALID_PARAMETER(10004, "期待值为空找不到想要的对象"),
    VERIFICATION_CODE_ERROR(10005, "验证码错误或已过期"),
    OLD_PASSWORD_ERROR(10006, "原密码错误"),
    EMAIL_DUPLICATION_ERROR(10007, "邮箱已被绑定"),
    USER_DUPLICATION_ERROR(10008, "该用户已存在"),
    USER_NOTEXIST_ERROR(10009, "用户不存在或已被禁用"),
    OTHER(99999, "期待值为空找不到想要的对象"),
    NO_AUTHORIZATION(40001, "请先登录"),
    UNAUTHORIZED_MODIFICATION(41000, "非法修改"),
    UNAUTHORIZED(41001, "权限不足,请联系管理员"),
    RATELIMIT(50001,"当前接口已达到最大限流次数");

    private Integer code;
    private String msg;

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return this.msg;
    }

    public static ErrorEnum getByCode(Integer code) {
        ErrorEnum[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ErrorEnum e = var1[var3];
            if (e.getCode().equals(code)) {
                return e;
            }
        }

        return null;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    private ErrorEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
