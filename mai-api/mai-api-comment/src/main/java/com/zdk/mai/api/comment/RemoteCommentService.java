package com.zdk.mai.api.comment;

import com.zdk.mai.common.core.constant.SecurityConstants;
import com.zdk.mai.common.core.constant.ServiceNameConstants;
import com.zdk.mai.common.core.response.CommonResult;
import com.zdk.mai.common.model.comment.vo.CommentVO;
import com.zdk.mai.common.model.user.vo.UserAchievementVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/3 11:02
 */
@FeignClient(name = ServiceNameConstants.COMMENT_SERVICE)
public interface RemoteCommentService {

    /**
     * 获取贴子评论数
     * @param postId
     * @param source
     * @return
     */
    @GetMapping("/comment/postCommentCount")
    Integer postCommentCount(@RequestParam("postId") Long postId, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 用户的评论相关统计数据
     * @param userId
     * @param source
     * @return
     */
    @GetMapping("/comment/userAchievement/{userId}")
    CommonResult<UserAchievementVO> userAchievement(@PathVariable("userId") Long userId, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);


    /**
     * 根据ids获取多条评论
     * @param commentId
     * @param source
     * @return
     */
    @GetMapping("/comment/getById/{commentId}")
    CommonResult<CommentVO> getById(@PathVariable("commentId") Long commentId, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

}
