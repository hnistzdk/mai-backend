package com.zdk.mai.common.model.post.request;

import com.zdk.mai.common.core.request.BaseSearchRequest;
import lombok.Data;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/2 10:52
 */
@Data
public class PostSearchRequest extends BaseSearchRequest {

    /**
     * 1是贴子 2是职言
     */
    private Integer type;

    private Long userId;

    /** 不在个人主页tab下才全部查*/
    private Boolean isTab;

}
