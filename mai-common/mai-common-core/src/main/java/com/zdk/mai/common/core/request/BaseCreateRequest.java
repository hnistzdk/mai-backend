package com.zdk.mai.common.core.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Description
 * @Author zdk
 * @Date 2022/12/4 19:15
 */
@Data
public class BaseCreateRequest extends BaseRequest {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(
            hidden = true
    )
    protected Long createBy;
    @ApiModelProperty(
            hidden = true
    )
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    protected Date createTime;
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
    @ApiModelProperty(
            hidden = true
    )
    protected Long sortOrder;
    @ApiModelProperty(
            hidden = true
    )
    protected Long rowVersion;
}

