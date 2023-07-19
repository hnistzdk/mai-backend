package com.zdk.mai.common.model.message.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zdk.mai.common.core.response.BaseVO;
import com.zdk.mai.common.model.comment.vo.CommentVO;
import com.zdk.mai.common.model.post.vo.PostDetailVO;
import lombok.Data;

import java.util.Date;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/27 16:40
 */
@Data
public class MessageVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    /**
     * 通知编号
     */
    private Long noticeId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 通知类型（0任务提醒，1系统通知）
     */
    private Integer noticeType;

    /** 消息类型（1收到回复、2收到赞、3新增粉丝，4系统通知） */
    private Integer messageType;

    /** 是否已读 */
    private Boolean readFlag;

    /**
     * 逻辑删除(0正常,1删除)
     */
    private Boolean delFlag;

    /**
     * 发起人Id
     */
    private Long senderId;

    /**
     * 发起人username
     */
    private String senderUsername;


    /**
     * 发起人头像
     */
    private String senderAvatar;


    /**
     * 操作的对象ID（贴子id、用户id等非评论id）
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
     * 评论id
     */
    private Long parentCommentId;

    /**
     * 操作的对象名称（用户名称、文章名、评论内容等）
     */
    private String title;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    private PostDetailVO postDetailVO;

    private CommentVO commentVO;


}
