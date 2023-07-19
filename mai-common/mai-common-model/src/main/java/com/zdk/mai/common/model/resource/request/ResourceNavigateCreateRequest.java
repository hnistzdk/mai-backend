package com.zdk.mai.common.model.resource.request;

import com.zdk.mai.common.core.request.BaseCreateRequest;
import lombok.Data;

/**
 * @Description
 * @Author zdk
 * @Date 2023/4/3 22:28
 */
@Data
public class ResourceNavigateCreateRequest extends BaseCreateRequest {

    /**
     * 资源导航编号
     */
    private Integer id;

    /**
     * 资源名字
     */
    private String resourceName;

    /**
     * logo(图片)
     */
    private String logo;

    /**
     * 类别
     */
    private String category;

    /**
     * 描述
     */
    private String description;

    /**
     * 链接
     */
    private String link;

}
