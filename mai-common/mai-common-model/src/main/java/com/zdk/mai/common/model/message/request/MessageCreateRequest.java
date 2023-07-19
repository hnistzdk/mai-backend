package com.zdk.mai.common.model.message.request;

import com.zdk.mai.common.core.request.BaseCreateRequest;
import lombok.Data;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/27 20:38
 */
@Data
public class MessageCreateRequest extends BaseCreateRequest {

    private Long senderId;

    private Long receiveId;

    private Long objectId;

    private Integer objectType;

    private Long commentId;

    private Long parentCommentId;

    private String content;

    private Integer messageType;

    private Integer noticeType;

    private String senderUsername;

    private String senderAvatar;


}
