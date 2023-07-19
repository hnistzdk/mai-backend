package com.zdk.mai.common.model.search.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zdk.mai.common.model.user.vo.UserInfoVO;
import lombok.Data;

import java.util.Date;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/29 19:03
 */
@Data
public class SearchPostVO {

    private Long postId;

    private String title;

    /**
     * 文章内容（纯文本）
     */
    private String content;


    /**
     * 高亮文本
     */
    private String highlightContent;


    /** 状态(0禁用 1启动) */
    private Boolean state;

    /** 贴子浏览量 */
    private Long pv;

    /** 置顶(数字大优先级越高) */
    private Long top;

    /** 发帖人id */
    private Long authorId;

    /** 发帖人用户名id */
    private String authorUsername;

    /** 发帖人头像 */
    private String avatar;

    /**
     * 贴子点赞数量
     */
    private Long likeCount;

    /**
     * 该用户是否已点赞此贴子
     */
    private Boolean like;

    /**
     * 贴子评论数
     */
    private Long commentCount;


    private Integer type;

    private String images;

    private UserInfoVO userInfoVO;

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
