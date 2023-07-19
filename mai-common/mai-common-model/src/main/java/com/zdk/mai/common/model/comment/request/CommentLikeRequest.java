package com.zdk.mai.common.model.comment.request;

import com.zdk.mai.common.core.request.BaseCreateRequest;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/7 12:54
 */
@Data
public class CommentLikeRequest extends BaseCreateRequest {

    /** 评论id*/
    @NotNull
    private Long commentId;

    /** false取消 true点赞 传来的当前的状态*/
    @NotNull
    private Boolean state;

}
