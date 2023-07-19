package com.zdk.mai.common.model.resource.request;

import com.zdk.mai.common.core.request.BaseSearchRequest;
import lombok.Data;

/**
 * @Description
 * @Author zdk
 * @Date 2023/4/3 22:29
 */
@Data
public class ResourceNavigateSearchRequest extends BaseSearchRequest {

    /**
     * 类别
     */
    private String category;
}
