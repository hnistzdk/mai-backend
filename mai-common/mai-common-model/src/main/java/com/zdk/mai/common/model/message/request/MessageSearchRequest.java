package com.zdk.mai.common.model.message.request;

import com.zdk.mai.common.core.request.BaseSearchRequest;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/27 16:41
 */
@Data
public class MessageSearchRequest extends BaseSearchRequest {

    @NotNull
    private Integer noticeType;

    @NotNull
    private Integer messageType;

}
