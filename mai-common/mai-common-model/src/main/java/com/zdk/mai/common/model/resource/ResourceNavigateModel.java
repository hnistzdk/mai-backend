package com.zdk.mai.common.model.resource;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zdk.mai.common.model.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Description
 * @Author zdk
 * @Date 2023/4/3 22:21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("mai_resource_navigate")
public class ResourceNavigateModel extends BaseModel {

    /**
     * 资源导航编号
     */
    @TableId(value = "id",type = IdType.AUTO)
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
