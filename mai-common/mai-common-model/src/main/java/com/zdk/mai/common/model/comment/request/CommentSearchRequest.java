package com.zdk.mai.common.model.comment.request;

import com.zdk.mai.common.core.request.BaseSearchRequest;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/3 15:00
 */
@Data
public class CommentSearchRequest extends BaseSearchRequest {

    @NotNull
    @Min(value = 1)
    private Long postId;

    /** 评论排序规则 newest or hottest*/
    private String sortRule;

}
