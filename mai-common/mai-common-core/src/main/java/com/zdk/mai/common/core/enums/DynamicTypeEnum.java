package com.zdk.mai.common.core.enums;

/**
 * @Description 动态类别枚举
 * @Author zdk
 * @Date 2023/3/24 19:23
 */
public enum DynamicTypeEnum {

    /**
     * 发帖
     */
    POST(1),
    /**
     * 评论贴子
     */
    POST_COMMENT(2),
    /**
     * 点赞贴子
     */
    POST_LIKE(3),
    /**
     * 评论点赞
     */
    COMMENT_LIKE(4),
    /**
     * 评论回复
     */
    COMMENT_REPLY(5),
    /**
     * 关注
     */
    FOLLOW(6);

    /**
     * 说明
     */
    private final Integer type;

    DynamicTypeEnum(int type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
