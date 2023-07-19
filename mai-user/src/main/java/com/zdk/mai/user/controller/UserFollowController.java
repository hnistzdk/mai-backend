package com.zdk.mai.user.controller;

import com.github.pagehelper.PageInfo;
import com.zdk.mai.common.core.response.CommonResult;
import com.zdk.mai.common.core.response.PageResponse;
import com.zdk.mai.common.log.annotation.AccessLog;
import com.zdk.mai.common.log.enums.BusinessType;
import com.zdk.mai.common.model.follow.UserFollowModel;
import com.zdk.mai.common.model.follow.request.FollowRequest;
import com.zdk.mai.common.model.follow.request.FollowSearchRequest;
import com.zdk.mai.common.model.follow.vo.FollowCountVO;
import com.zdk.mai.common.model.follow.vo.FollowInfoVO;
import com.zdk.mai.common.model.user.vo.UserInfoVO;
import com.zdk.mai.common.security.annotation.InitRequestArg;
import com.zdk.mai.common.security.annotation.NotRequiresLogin;
import com.zdk.mai.common.security.annotation.RateLimiter;
import com.zdk.mai.common.security.annotation.ArgType;
import com.zdk.mai.user.service.IUserFollowService;
import com.zdk.mai.user.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/20 16:12
 */
@Api(tags = "用户关注相关接口")
@Validated
@RestController
public class UserFollowController {

    @Autowired
    private IUserFollowService userFollowService;

    @Autowired
    private IUserService userService;

    @Autowired
    @Qualifier("mailTaskExecutor")
    public Executor taskExecutor;


    @ApiOperation(value = "关注状态改变", notes = "关注状态改变")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "toUser", value = "toUser", required = true, dataType = "String",paramType = "path"),
    })
    @RateLimiter(prefix = "user",key = "follow",expireTime = 2,maxCount = 1,rateMessage = "操作过于频繁")
    @InitRequestArg(argType = ArgType.CREATE)
    @PostMapping("/user/follow")
    public CommonResult follow(@RequestBody @Valid FollowRequest followRequest) {

        userFollowService.follow(followRequest);

        return CommonResult.success();
    }


    @ApiOperation(value = "关注列表", notes = "关注列表")
    @AccessLog(title = "关注/粉丝列表查询",businessType = BusinessType.SELECT)
    @NotRequiresLogin
    @GetMapping("/user/follow/list")
    public CommonResult list(FollowSearchRequest searchRequest){

        PageInfo<UserFollowModel> pageInfo = userFollowService.list(searchRequest);

        List<FollowInfoVO> voList = getFollowInfoVOList(pageInfo,searchRequest.getType());

        return CommonResult.success(PageResponse.pageResponse(voList, pageInfo));
    }

    @ApiOperation(value = "关注统计", notes = "关注统计")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "userId", required = true, dataType = "Long"),
    })
    @NotRequiresLogin
    @GetMapping("/user/follow/followCount")
    public CommonResult<FollowCountVO> followCount(@RequestParam("userId") Long userId){

        FollowCountVO vo = userFollowService.followCount(userId);

        return CommonResult.success(vo);
    }

    /**
     * 获取vo
     * @param pageInfo
     * @return
     */
    private List<FollowInfoVO> getFollowInfoVOList(PageInfo<UserFollowModel> pageInfo,Integer type){

        List<FollowInfoVO> voList = new ArrayList<>(pageInfo.getSize());

        List<CompletableFuture<UserInfoVO>> userInfoFutureList = new ArrayList<>(pageInfo.getSize());
        for (UserFollowModel followModel : pageInfo.getList()) {
            if (type == 1){
                // 1 查询被被关注者信息
                userInfoFutureList.add(CompletableFuture.supplyAsync(()->userService.profile(followModel.getToUser()),taskExecutor));
            }else {
                userInfoFutureList.add(CompletableFuture.supplyAsync(()->userService.profile(followModel.getFromUser()),taskExecutor));
            }
        }

        List<UserInfoVO> userInfoVOs = userInfoFutureList.stream().map(CompletableFuture::join).collect(Collectors.toList());

        for (int i = 0; i < pageInfo.getList().size(); i++) {

            UserFollowModel followModel = pageInfo.getList().get(i);

            FollowInfoVO vo = new FollowInfoVO();

            BeanUtils.copyProperties(followModel, vo);

            vo.setUserInfo(userInfoVOs.get(i));

            voList.add(vo);
        }
        return voList;
    }




}
