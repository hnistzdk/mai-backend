package com.zdk.mai.common.core.enums.message;

/**
 * @Description 消息操作对象类型枚举
 * @Author zdk
 * @Date 2023/4/2 17:24
 */
public enum ObjectTypeEnum {

    /**
     * 回复、点赞贴子
     */
    POST(1),
    /**
     * 回复、点赞评论
     */
    COMMENT(2),
    /**
     * 关注用户
     */
    USER(3);

    private final Integer objectType;

    ObjectTypeEnum(Integer objectType) {
        this.objectType = objectType;
    }

    public Integer getObjectType() {
        return objectType;
    }
}
