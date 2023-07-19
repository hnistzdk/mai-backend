package com.zdk.mai.comment.controller;

import com.zdk.mai.comment.service.ICommentLikeService;
import com.zdk.mai.common.core.response.CommonResult;
import com.zdk.mai.common.model.comment.request.CommentLikeRequest;
import com.zdk.mai.common.security.annotation.InitRequestArg;
import com.zdk.mai.common.security.annotation.ArgType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/6 16:41
 */
@Api(tags = "评论点赞接口")
@Validated
@RestController
public class CommentLikeController {


    @Autowired
    private ICommentLikeService commentLikeService;


    @ApiOperation(value = "点赞操作", notes = "点赞操作")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "commentLikeRequest", value = "", required = true, dataType = "CommentLikeRequest"),
    })
    @InitRequestArg(argType = ArgType.CREATE)
    @PostMapping("/comment/like")
    public CommonResult create(@RequestBody CommentLikeRequest commentLikeRequest){
        commentLikeService.like(commentLikeRequest);
        return CommonResult.success();
    }

}
