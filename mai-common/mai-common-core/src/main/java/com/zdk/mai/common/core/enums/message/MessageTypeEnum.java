package com.zdk.mai.common.core.enums.message;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/27 20:34
 */
public enum MessageTypeEnum {

    /**
     * 收到回复
     */
    REPLY(1),
    /**
     * 收到点赞(贴子、评论)
     */
    LIKE(2),
    /**
     * 收到关注
     */
    FOLLOW(3),
    /**
     * 系统通知
     */
    SYSTEM(4);

    private final Integer messageType;

    MessageTypeEnum(Integer messageType) {
        this.messageType = messageType;
    }

    public Integer getMessageType() {
        return messageType;
    }
}
