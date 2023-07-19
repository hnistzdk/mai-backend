package com.zdk.mai.common.model.dynamic.request;

import lombok.Data;

import java.util.Date;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/27 23:04
 */
@Data
public class DynamicDeleteRequest {

    private static final long serialVersionUID = 1L;

    private Long objectId;

    private Long commentId;

    private Integer type;

    private Date updateTime;

}
