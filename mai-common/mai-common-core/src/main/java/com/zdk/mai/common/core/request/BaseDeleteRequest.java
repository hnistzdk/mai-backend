package com.zdk.mai.common.core.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Description
 * @Author zdk
 * @Date 2022/12/4 19:18
 */
@Data
public abstract class BaseDeleteRequest extends BaseRequest {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(
            hidden = true
    )
    protected Long updateBy;
    @ApiModelProperty(
            hidden = true
    )
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    protected Date updateTime;
    @ApiModelProperty(
            hidden = true
    )
    protected Boolean delFlag;
}
