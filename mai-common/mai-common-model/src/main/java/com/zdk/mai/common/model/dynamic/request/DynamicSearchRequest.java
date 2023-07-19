package com.zdk.mai.common.model.dynamic.request;

import com.zdk.mai.common.core.request.BaseSearchRequest;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/24 21:12
 */
@Data
public class DynamicSearchRequest extends BaseSearchRequest {

    @NotNull(message = "用户id不能为空")
    private Long userId;
}
