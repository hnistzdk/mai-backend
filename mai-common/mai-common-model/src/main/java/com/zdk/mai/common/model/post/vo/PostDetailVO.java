package com.zdk.mai.common.model.post.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zdk.mai.common.core.response.BaseVO;
import com.zdk.mai.common.model.user.vo.UserInfoVO;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/1 21:56
 */
@Data
public class PostDetailVO extends BaseVO {

    private Long postId;

    private String title;

    /**
     * 文章内容（纯文本）
     */
    private String content;

    /**
     * 文章内容（html）
     */
    private String html;

    /**
     * 文章内容（markdown）
     */
    private String markdown;

    /** 状态(0禁用 1启动) */
    private Boolean state;

    /** 贴子浏览量 */
    private Long pv;

    /** 置顶(数字大优先级越高) */
    private Boolean top;

    /** 发帖人id */
    private Long authorId;

    /** 发帖人用户名id */
    private String authorUsername;

    /** 发帖人头像 */
    private String avatar;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

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
}
