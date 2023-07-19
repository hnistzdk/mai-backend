package com.zdk.mai.common.model.message;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zdk.mai.common.model.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Description 消息信息
 * @Author zdk
 * @Date 2023/3/27 15:59
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("mai_message_info")
public class MessageInfoModel extends BaseModel {

    /** 消息编号 */
    @TableId(value = "message_id",type = IdType.ASSIGN_ID)
    private Long messageId;

    /** 消息内容 */
    private String content;

    /** 消息类型（1收到回复、2收到赞、3新增粉丝，4系统通知） */
    private Integer messageType;

    /**
     * 操作对象id
     */
    private Long objectId;

    /**
     * 操作对象类型(1贴子、2评论、3用户)
     */
    private Integer objectType;

    /**
     * 评论id
     */
    private Long commentId;

    /**
     * 父评论id
     */
    private Long parentCommentId;

}
