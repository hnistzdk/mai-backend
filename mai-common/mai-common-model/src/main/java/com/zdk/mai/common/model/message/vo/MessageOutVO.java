package com.zdk.mai.common.model.message.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zdk.mai.common.core.response.BaseVO;
import lombok.Data;

import java.util.Date;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/27 16:42
 */
@Data
public class MessageOutVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    /**
     * 通知编号
     */
    private Long id;

    /**
     * 是否已读（0未读，1已读）
     */
    private Boolean read;

    /**
     * 项目id
     */
    private Long projectId;

    /**
     * 系统名称
     */
    private String projectName;

    /**
     * 消息内容
     */
    private String message;

    /**
     * 消息类型（0任务提醒，1系统通知）
     */
    private Integer type;

    /**
     * 逻辑删除(0正常,1删除)
     */
    private Boolean delFlag;

    /**
     * 创建用户id
     */
    private Long createBy;

    /**
     * 创建用户名称
     */
    private String createUsername;

    /**
     * 更新用户id
     */
    private Long updateBy;

    /**
     * 更新用户名称
     */
    private String updateUsername;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;


}
