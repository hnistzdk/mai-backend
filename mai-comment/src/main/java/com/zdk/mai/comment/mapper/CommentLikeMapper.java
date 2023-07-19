package com.zdk.mai.comment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zdk.mai.common.model.comment.CommentLikeModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/6 16:43
 */
@Mapper
public interface CommentLikeMapper extends BaseMapper<CommentLikeModel> {
    /**
     * 获取用户评论获赞总数
     * @param userId
     * @return
     */
    Long getUserCommentLikeCount(@Param("userId") Long userId);
}
