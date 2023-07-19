package com.zdk.mai.common.model.follow.request;

import com.zdk.mai.common.core.request.BaseCreateRequest;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/20 16:26
 */
@Data
public class FollowRequest extends BaseCreateRequest {

    @NotNull
    private Long toUser;

    @NotBlank
    private String toUsername;

    /** 用于异步时防止userId丢失*/
    private Long fromUser;

}
