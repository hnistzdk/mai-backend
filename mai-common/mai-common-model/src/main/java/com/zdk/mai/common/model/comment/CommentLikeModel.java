package com.zdk.mai.common.model.comment;

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
 * @Date 2023/3/3 11:29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("mai_comment_like")
public class CommentLikeModel extends BaseModel {

    @TableId(value = "comment_like_id",type = IdType.ASSIGN_ID)
    private Long commentLikeId;

    /** 评论id*/
    private Long commentId;

    /** false取消 true点赞*/
    private Boolean state;
}
