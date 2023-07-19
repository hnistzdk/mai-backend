package com.zdk.mai.common.model.search.request;

import com.zdk.mai.common.core.request.BaseSearchRequest;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/29 19:05
 */
@Data
public class SearchRequest extends BaseSearchRequest {

    @NotBlank
    private String query;

    @NotNull
    private Integer type;

    private String sortRule;

}
