package com.zdk.mai.common.es;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/21 21:16
 */
@Data
public class EsBaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 创建者 */
    private Long createBy;

    /** 创建时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 更新者 */
    private Long updateBy;


    /** 更新时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
