package com.zdk.mai.common.model.comment.request;

import com.zdk.mai.common.core.request.BaseCreateRequest;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/3 15:20
 */
@Data
public class CommentCreateRequest extends BaseCreateRequest {

    /** 父级评论id*/
    private Long parentId;

    /** 根评论id*/
    private Long rootId;

    /** 贴子id*/
    @NotNull
    @Min(value = 1)
    private Long postId;

    /** 评论内容*/
    @NotBlank
    @Length(max = 200,message = "评论最多只能5字")
    private String content;

    private Long postUserId;

    private String postUsername;
}
