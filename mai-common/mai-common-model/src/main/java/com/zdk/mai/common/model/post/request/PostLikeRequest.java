package com.zdk.mai.common.model.post.request;

import com.zdk.mai.common.core.request.BaseCreateRequest;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/8 16:42
 */
@Data
public class PostLikeRequest extends BaseCreateRequest {

    /** 贴子id*/
    @NotNull
    private Long postId;

    /** false取消 true点赞 传来的是当前的状态*/
    @NotNull
    private Boolean state;

}
