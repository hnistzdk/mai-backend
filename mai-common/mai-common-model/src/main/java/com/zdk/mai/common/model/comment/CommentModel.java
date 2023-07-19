package com.zdk.mai.common.model.comment;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zdk.mai.common.model.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/2 19:35
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("mai_comment")
public class CommentModel extends BaseModel {

    @TableId(value = "comment_id",type = IdType.ASSIGN_ID)
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
