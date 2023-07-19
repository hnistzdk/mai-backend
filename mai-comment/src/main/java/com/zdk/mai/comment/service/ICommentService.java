package com.zdk.mai.comment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zdk.mai.common.model.comment.CommentModel;
import com.zdk.mai.common.model.comment.request.CommentCreateRequest;
import com.zdk.mai.common.model.comment.request.CommentSearchRequest;
import com.zdk.mai.common.model.comment.vo.CommentVO;
import com.zdk.mai.common.model.post.vo.PostDetailVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/2 19:34
 */
public interface ICommentService extends IService<CommentModel> {


    /**
     * 创建评论
     * @param createRequest
     */
    void create(CommentCreateRequest createRequest);


    /**
     * 根据贴子查询评论
     * @param searchRequest
     * @return
     */
    PageInfo<CommentModel> list(CommentSearchRequest searchRequest);


    /**
     * 构造VO
     * @param toPost
     * @param sortRule
     * @return
     */
    List<CommentVO> buildCommentVOList(List<CommentModel> toPost,String sortRule);

    /**
     * 根据评论id删除评论
     * @param commentId
     * @param postId
     */
    void delete(Long commentId,Long postId);

    /**
     * 根据id获取评论
     * @param commentId
     * @return
     */
    CommentVO getById(Long commentId);

}
