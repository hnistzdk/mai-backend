package com.zdk.mai.common.core.enums;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/24 21:38
 */
public enum PostTypeEnum {

    /**
     * 有标题的贴子
     */
    POST(1),
    /**
     * 职言
     */
    GOSSIP(2);

    private Integer type;


    PostTypeEnum(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
