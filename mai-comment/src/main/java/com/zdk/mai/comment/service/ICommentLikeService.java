package com.zdk.mai.comment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zdk.mai.common.model.comment.CommentLikeModel;
import com.zdk.mai.common.model.comment.request.CommentLikeRequest;
import com.zdk.mai.common.model.user.vo.UserAchievementVO;
import org.apache.ibatis.annotations.Param;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/6 16:41
 */
public interface ICommentLikeService extends IService<CommentLikeModel> {

    /**
     * 点赞操作
     * @param commentLikeRequest
     */
    void like(CommentLikeRequest commentLikeRequest);

    /**
     * 用户是否已点赞该评论
     * @param commentId
     * @return
     */
    Boolean userLikeOrNot(Long commentId);

    /**
     * 获取评论点赞数
     * @param commentId
     * @return
     */
    Long getLikeCount(Long commentId);

    /**
     * 获取用户评论获赞总数
     * @param userId
     * @return
     */
    UserAchievementVO getUserCommentLikeCount(Long userId);
}
