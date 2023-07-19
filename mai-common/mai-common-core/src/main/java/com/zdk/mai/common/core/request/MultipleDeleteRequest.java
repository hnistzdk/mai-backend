package com.zdk.mai.common.core.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description
 * @Author zdk
 * @Date 2022/12/4 19:19
 */
@Data
public class MultipleDeleteRequest extends BaseDeleteRequest {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(
            name = "id",
            value = "需要删除的id",
            example = "1002",
            dataType = "long",
            required = true
    )
    private List<Long> ids;
}
