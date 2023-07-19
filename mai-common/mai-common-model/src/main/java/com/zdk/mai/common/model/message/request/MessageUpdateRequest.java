package com.zdk.mai.common.model.message.request;

import com.zdk.mai.common.core.request.BaseUpdateRequest;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Description
 * @Author zdk
 * @Date 2023/4/2 21:21
 */
@Data
public class MessageUpdateRequest extends BaseUpdateRequest {

    private Long noticeId;

    private Boolean readFlag;

    @NotNull
    private Long userId;

    private Integer messageType;

    private Integer noticeType;

}
