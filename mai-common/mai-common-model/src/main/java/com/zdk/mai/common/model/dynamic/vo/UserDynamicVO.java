package com.zdk.mai.common.model.dynamic.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/24 21:08
 */
@Data
public class UserDynamicVO {

    private static final long serialVersionUID = 1L;

    /**
     * 用户动态编号
     */
    private Long dynamicId;

    /**
     * 类型（写文章、评论、点赞、关注等）
     */
    private Integer type;

    /**
     * 发起人
     */
    private Long userId;

    /**
     * 发起人名称
     */
    private String username;

    /**
     * 发起人（头像）
     */
    private String avatar;

    /**
     * 操作的对象ID（文章id、用户id等非评论id）
     */
    private Long objectId;

    /**
     * 评论id
     */
    private Long commentId;

    /**
     * 操作的对象名称（用户名称、文章名、评论内容等）
     */
    private String title;

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
