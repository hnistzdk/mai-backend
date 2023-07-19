package com.zdk.mai.comment.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.zdk.mai.comment.service.ICommentLikeService;
import com.zdk.mai.comment.service.ICommentService;
import com.zdk.mai.common.core.response.CommonResult;
import com.zdk.mai.common.core.response.PageResponse;
import com.zdk.mai.common.model.comment.CommentModel;
import com.zdk.mai.common.model.comment.request.CommentCreateRequest;
import com.zdk.mai.common.model.comment.request.CommentSearchRequest;
import com.zdk.mai.common.model.comment.vo.CommentVO;
import com.zdk.mai.common.model.user.vo.UserAchievementVO;
import com.zdk.mai.common.security.annotation.ArgType;
import com.zdk.mai.common.security.annotation.InitRequestArg;
import com.zdk.mai.common.security.annotation.InnerAuth;
import com.zdk.mai.common.security.annotation.NotRequiresLogin;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/2 19:33
 */
@Api(tags = "评论接口")
@Validated
@RestController
public class CommentController {

    @Autowired
    private ICommentService commentService;

    @Autowired
    private ICommentLikeService commentLikeService;


    @ApiOperation(value = "创建评论", notes = "创建评论")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "createRequest", value = "createRequest", required = true, dataType = "CommentCreateRequest"),
    })
    @InitRequestArg(argType = ArgType.CREATE)
    @PostMapping("/comment/create")
    public CommonResult create(@RequestBody @Valid CommentCreateRequest createRequest){
        commentService.create(createRequest);
        return CommonResult.success();
    }

    @ApiOperation(value = "根据贴子获取评论", notes = "根据贴子获取评论")
    @NotRequiresLogin
    @GetMapping("/comment/list")
    public CommonResult list(CommentSearchRequest searchRequest){
        PageInfo<CommentModel> pageInfo = commentService.list(searchRequest);
        String sortRule = searchRequest.getSortRule();
        List<CommentVO> voList = commentService.buildCommentVOList(pageInfo.getList(), sortRule);
        return CommonResult.success(PageResponse.pageResponse(voList, pageInfo));
    }


    @ApiOperation(value = "获取贴子评论数", notes = "获取贴子评论数")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "postId", value = "postId", required = true, dataType = "Long"),
    })
    @NotRequiresLogin
    @InnerAuth
    @GetMapping("/comment/postCommentCount")
    @Cacheable(cacheNames = "comment",key = "'comment_count:'+#postId")
    public Integer postCommentCount(@RequestParam("postId") Long postId){
        return commentService.count(new LambdaQueryWrapper<CommentModel>().eq(CommentModel::getPostId, postId));
    }


    @ApiOperation(value = "删除评论", notes = "删除评论")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "commentId", value = "commentId", required = true, dataType = "Long",paramType = "path"),
    })
    @PostMapping("/comment/delete/{commentId}/{postId}")
    public CommonResult delete(@PathVariable("commentId") Long commentId,@PathVariable("postId") Long postId){
        commentService.delete(commentId,postId);
        return CommonResult.success();
    }


    @ApiOperation(value = "用户的评论相关统计数据", notes = "用户的评论相关统计数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "userId", required = true, dataType = "Long",paramType = "path"),
    })
    @InnerAuth
    @GetMapping("/comment/userAchievement/{userId}")
    public CommonResult<UserAchievementVO> userAchievement(@PathVariable("userId") Long userId){

        UserAchievementVO vo = commentLikeService.getUserCommentLikeCount(userId);

        return CommonResult.success(vo);
    }


    @ApiOperation(value = "id批量获取评论", notes = "id批量获取评论")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "userId", required = true, dataType = "Long",paramType = "path"),
    })
    @InnerAuth
    @GetMapping("/comment/getById/{commentId}")
    public CommonResult<CommentVO> getByIds(@PathVariable("commentId") Long commentId){
        return CommonResult.success(commentService.getById(commentId));

    }


}
