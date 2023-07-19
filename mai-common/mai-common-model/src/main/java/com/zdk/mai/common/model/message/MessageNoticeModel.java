package com.zdk.mai.common.model.message;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zdk.mai.common.model.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Description 通知动作
 * @Author zdk
 * @Date 2023/3/27 16:00
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("mai_message_notice")
public class MessageNoticeModel extends BaseModel {

    /** 通知编号 */
    @TableId(value = "notice_id",type = IdType.ASSIGN_ID)
    private Long noticeId;

    /** 通知类型(0任务提醒、1系统通知) */
    private Integer noticeType;

    /** 消息id */
    private Long messageId;

    /** 冗余一下消息类型 */
    private Integer messageType;

    /** 发送者id */
    private Long senderId;

    /** 接收者id */
    private Long receiveId;


    private String senderUsername;


    private String senderAvatar;

    /** 是否已读(0未读、1已读) */
    private Boolean readFlag;

}
