package com.zdk.mai.common.model.post.request;

import com.zdk.mai.common.core.request.SingleDeleteRequest;
import lombok.Data;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/25 18:52
 */
@Data
public class PostDeleteRequest extends SingleDeleteRequest {

    private Long postId;

}
