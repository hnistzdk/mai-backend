package com.zdk.mai.common.model.message.request;

import com.zdk.mai.common.core.request.SingleDeleteRequest;
import lombok.Data;

/**
 * @Description
 * @Author zdk
 * @Date 2023/4/14 13:16
 */
@Data
public class MessageDeleteRequest extends SingleDeleteRequest {

    private Integer objectType;

    private Long objectId;

    private Long commentId;

}
