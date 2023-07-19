package com.zdk.mai.common.core.enums;

/**
 * @Description 用户状态枚举
 * @Author zdk
 * @Date 2022/11/30 17:47
 */
public enum UserStatusEnum {
    /**
     * 正常
     */
    OK("0", "正常"),
    DISABLE("1", "停用"),
    DELETED("2", "删除");

    private final String code;

    private final String info;

    UserStatusEnum(String code, String info) {
        this.code = code;
        this.info = info;
    }

    public String getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }
}
