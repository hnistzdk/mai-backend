package com.zdk.mai.common.core.enums.message;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/27 21:10
 */
public enum NoticeTypeEnum {

    /**
     * 系统通知
     */
    SYSTEM_NOTICE(1),

    /**
     * 任务提醒
     */
    TASK_REMINDER(0);

    private final Integer noticeType;


    NoticeTypeEnum(Integer noticeType) {
        this.noticeType = noticeType;
    }

    public Integer getNoticeType() {
        return noticeType;
    }
}
