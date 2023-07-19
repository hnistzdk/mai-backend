package com.zdk.mai.post.controller;

import com.zdk.mai.common.core.response.CommonResult;
import com.zdk.mai.common.model.post.request.PostLikeRequest;
import com.zdk.mai.common.security.annotation.InitRequestArg;
import com.zdk.mai.common.security.annotation.ArgType;
import com.zdk.mai.post.service.IPostLikeService;
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
 * @Date 2023/3/8 16:46
 */
@Api(tags = "点赞接口")
@Validated
@RestController
public class PostLikeController {

    @Autowired
    private IPostLikeService postLikeService;


    @ApiOperation(value = "点赞操作", notes = "点赞操作")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "postLikeRequest", value = "", required = true, dataType = "PostLikeRequest"),
    })
    @InitRequestArg(argType = ArgType.CREATE)
    @PostMapping("/post/like")
    public CommonResult create(@RequestBody PostLikeRequest postLikeRequest){
        postLikeService.like(postLikeRequest);
        return CommonResult.success();
    }
}
