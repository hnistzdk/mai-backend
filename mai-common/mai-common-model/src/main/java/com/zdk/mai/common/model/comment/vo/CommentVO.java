package com.zdk.mai.common.model.comment.vo;

import com.zdk.mai.common.model.comment.CommentModel;
import lombok.Data;

import java.util.List;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/3 15:40
 */
@Data
public class CommentVO extends CommentModel {

    /**
     * 回复数量
     */
    private Long repliesCount;

    /**
     * 评论深度
     */
    private Long depth;

    /**
     * 评论点赞数量
     */
    private Long likeCount;

    /**
     * 该用户是否已点赞此评论
     */
    private Boolean like;

    /**
     * 评论者头像
     */
    private String avatar;

    /**
     * 子评论list
     */
    private List<CommentVO> child;

    /**
     * 子评论list
     */
    private List<CommentVO> toComment;
}
