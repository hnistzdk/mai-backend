package com.zdk.mai.post.controller;

import com.github.pagehelper.PageInfo;
import com.zdk.mai.common.core.response.CommonResult;
import com.zdk.mai.common.core.response.PageResponse;
import com.zdk.mai.common.model.post.PostModel;
import com.zdk.mai.common.model.post.request.PostSearchRequest;
import com.zdk.mai.common.model.user.UserModel;
import com.zdk.mai.common.model.user.vo.UserInfoVO;
import com.zdk.mai.post.service.IPostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description
 * @Author zdk
 * @Date 2023/4/6 19:17
 */
@Slf4j
@Api(tags = {"管理端贴子/职言接口"})
@Validated
@RestController
@RequestMapping("/admin/post")
public class AdminPostController {


    @Autowired
    private IPostService postService;


    @ApiOperation(value = "管理端贴子/职言列表", notes = "管理端贴子/职言列表")
    @GetMapping("/list")
    public CommonResult list(PostSearchRequest searchRequest){

        PageInfo<PostModel> pageInfo = postService.list(searchRequest);

        return CommonResult.success(PageResponse.pageResponse(pageInfo.getList(), pageInfo));
    }

}
