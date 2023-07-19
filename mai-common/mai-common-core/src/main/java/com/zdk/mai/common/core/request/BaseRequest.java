package com.zdk.mai.common.core.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description
 * @Author zdk
 * @Date 2022/12/4 18:35
 */
@Data
public abstract class BaseRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(
            name = "appKey",
            value = "请求序号(nonce机制防重放攻击,建议使用UUID+时间戳)",
            dataType = "String"
    )
    private String appKey;
    @ApiModelProperty(
            hidden = true
    )
    private String appSecret;
    @ApiModelProperty(
            name = "requestId",
            value = "请求序号(nonce机制防重放攻击,建议使用UUID+时间戳)",
            dataType = "String"
    )
    private String requestId;
    @ApiModelProperty(
            name = "timestamp",
            value = "请求时间戳(timestamp机制防重放攻击,默认300s)",
            dataType = "Long"
    )
    private Long timestamp;
    @ApiModelProperty(
            name = "signature",
            value = "SHA256withRSA参数签名(此参数不作为源进行验签)",
            dataType = "String"
    )
    private String signature;
}
