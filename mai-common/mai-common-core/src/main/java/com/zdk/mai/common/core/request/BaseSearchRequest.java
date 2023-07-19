package com.zdk.mai.common.core.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description
 * @Author zdk
 * @Date 2022/12/4 18:36
 */
@Data
public class BaseSearchRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(
            name = "keywords",
            value = "搜索关键字",
            example = "关键字",
            dataType = "String"
    )
    protected String keywords;

    @ApiModelProperty(
            name = "pageSize",
            value = "每页大小(默认10)",
            example = "10",
            dataType = "Integer"
    )
    protected Integer pageSize = 10;
    @ApiModelProperty(
            name = "currentPage",
            value = "当前页码(默认1)",
            example = "1",
            dataType = "Integer"
    )
    protected Integer currentPage = 1;
    @ApiModelProperty(
            name = "sort",
            value = "排序字段，默认驼峰法转换字段名",
            example = "1",
            dataType = "String"
    )
    protected String sort;
    @ApiModelProperty(
            name = "order",
            value = "排序方式(desc|asc)",
            example = "1",
            dataType = "String"
    )
    protected String order;
}
