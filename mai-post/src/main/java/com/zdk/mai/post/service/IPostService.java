package com.zdk.mai.post.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zdk.mai.common.model.post.PostModel;
import com.zdk.mai.common.model.post.request.PostCreateRequest;
import com.zdk.mai.common.model.post.request.PostSearchRequest;
import com.zdk.mai.common.model.post.request.PostUpdateRequest;
import com.zdk.mai.common.model.post.vo.PostDetailVO;
import com.zdk.mai.common.model.post.vo.PostStatisticalVO;
import com.zdk.mai.common.model.user.vo.UserAchievementVO;

import java.util.List;

/**
 * @Description
 * @Author zdk
 * @Date 2023/2/28 17:27
 */
public interface IPostService extends IService<PostModel> {

    /**
     * 发帖操作
     * @param createRequest
     */
    void create(PostCreateRequest createRequest);

    /**
     * 贴子分页查询
     * @param searchRequest
     * @return
     */
    PageInfo<PostModel> list(PostSearchRequest searchRequest);

    /**
     * 获取文章统计信息
     * @param postId
     * @return
     */
    PostStatisticalVO statistical(Long postId);


    /**
     * 用户与贴子相关的成就
     * @param userId
     * @return
     */
    UserAchievementVO userAchievement(Long userId);


    /**
     * 贴子详情
     * @param postId
     * @return
     */
    PostDetailVO detail(Long postId);

    /**
     * 根据id列表获取多篇贴子
     * @param ids
     * @return
     */
    List<PostDetailVO> getByIds(List<Long> ids);

    /**
     * 删除贴子
     * @param postId
     */
    void delete(Long postId);


    /**
     * 点赞过的贴子
     * @param searchRequest
     * @return
     */
    PageInfo<PostModel> likeList(PostSearchRequest searchRequest);

    /**
     * 更新贴子信息
     * @param updateRequest
     */
    void update(PostUpdateRequest updateRequest);

}
