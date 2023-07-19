package com.zdk.mai.common.model.comment.request;

import lombok.Data;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/28 13:26
 */
@Data
public class CommentDeleteRequest {


    private Long postId;

    private Long commentId;

}
