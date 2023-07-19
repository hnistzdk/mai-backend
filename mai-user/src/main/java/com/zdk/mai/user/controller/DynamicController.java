package com.zdk.mai.user.controller;

import com.github.pagehelper.PageInfo;
import com.zdk.mai.common.core.constant.SecurityConstants;
import com.zdk.mai.common.core.response.CommonResult;
import com.zdk.mai.common.core.response.PageResponse;
import com.zdk.mai.common.log.annotation.AccessLog;
import com.zdk.mai.common.log.enums.BusinessType;
import com.zdk.mai.common.model.dynamic.UserDynamicModel;
import com.zdk.mai.common.model.dynamic.request.DynamicSearchRequest;
import com.zdk.mai.common.model.dynamic.vo.UserDynamicVO;
import com.zdk.mai.common.model.post.PostModel;
import com.zdk.mai.common.model.post.request.PostSearchRequest;
import com.zdk.mai.common.model.post.vo.PostDetailVO;
import com.zdk.mai.common.security.annotation.NotRequiresLogin;
import com.zdk.mai.user.service.IUserDynamicService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/11 11:13
 */
@Api(tags = "动态接口")
@Validated
@RestController
public class DynamicController {


    @Autowired
    private IUserDynamicService userDynamicService;

    @ApiOperation(value = "动态", notes = "动态")
    @AccessLog(title = "动态列表",businessType = BusinessType.SELECT)
    @NotRequiresLogin
    @GetMapping("/user/dynamic/list")
    public CommonResult list(@Valid DynamicSearchRequest searchRequest){
        PageInfo<UserDynamicModel> pageInfo = userDynamicService.list(searchRequest);
        List<UserDynamicVO> voList = userDynamicService.buildVOList(pageInfo,searchRequest.getUserId());
        return CommonResult.success(PageResponse.pageResponse(voList, pageInfo));
    }



}
