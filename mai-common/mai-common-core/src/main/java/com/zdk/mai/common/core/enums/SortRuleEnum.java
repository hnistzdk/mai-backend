package com.zdk.mai.common.core.enums;

/**
 * @Description 排序规则枚举
 * @Author zdk
 * @Date 2023/3/3 16:18
 */
public enum SortRuleEnum {
    /**
     * 最热
     */
    HOTTEST("最热"),
    NEWEST("最新");

    /**
     * 说明
     */
    private final String name;

    SortRuleEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
