package com.zdk.mai.common.es.model;

import cn.easyes.annotation.IndexId;
import cn.easyes.annotation.IndexName;
import cn.easyes.annotation.rely.IdType;
import lombok.Data;

import java.util.List;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/23 14:56
 */
@Data
@IndexName("comment")
public class EsCommentModel {

    @IndexId(type = IdType.CUSTOMIZE)
    private Long commentId;

    /** 根评论id*/
    private Long rootId;

    /** 父级评论id*/
    private Long parentId;

    /** 父评论者的id*/
    private Long parentUserId;

    /** 父评论者的用户名*/
    private String parentUsername;

    /** 贴子id*/
    private Long postId;

    /** 发帖人的id*/
    private Long postUserId;

    /** 发帖人的用户名*/
    private String postUsername;

    /** 评论内容*/
    private String content;

    /** 评论者用户名*/
    private String createUsername;
}
