package com.zdk.mai.post.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zdk.mai.common.model.post.request.PostLikeRequest;
import com.zdk.mai.common.model.post.PostLikeModel;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/8 16:47
 */
public interface IPostLikeService extends IService<PostLikeModel> {

    /**
     * 点赞操作
     * @param postLikeRequest
     */
    void like(PostLikeRequest postLikeRequest);

    /**
     * 用户是否已点赞该贴子
     * @param postId
     * @return
     */
    Boolean userLikeOrNot(Long postId);

    /**
     * 获取贴子点赞数
     * @param postId
     * @return
     */
    Long getLikeCount(Long postId);

    /**
     * 获取用户贴子点赞总数
     * @param userId
     * @return
     */
    Integer getUserPostLikeCount(Long userId);

}
