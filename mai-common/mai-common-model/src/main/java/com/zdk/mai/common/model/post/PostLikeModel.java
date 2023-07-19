package com.zdk.mai.common.model.post;

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
 * @Date 2023/3/3 11:31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("mai_post_like")
public class PostLikeModel extends BaseModel {

    @TableId(value = "post_like_id",type = IdType.ASSIGN_ID)
    private Long postLikeId;

    /** 贴子id*/
    private Long postId;

    /** false取消 true点赞*/
    private Boolean state;
}
