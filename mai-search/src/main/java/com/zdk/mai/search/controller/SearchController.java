package com.zdk.mai.search.controller;

import cn.easyes.core.biz.EsPageInfo;
import com.github.pagehelper.PageInfo;
import com.zdk.mai.api.user.RemoteUserService;
import com.zdk.mai.common.core.response.CommonResult;
import com.zdk.mai.common.core.response.PageResponse;
import com.zdk.mai.common.es.model.EsPostModel;
import com.zdk.mai.common.es.model.EsUserModel;
import com.zdk.mai.common.log.annotation.AccessLog;
import com.zdk.mai.common.log.enums.BusinessType;
import com.zdk.mai.common.model.search.request.SearchRequest;
import com.zdk.mai.common.model.search.vo.SearchPostVO;
import com.zdk.mai.common.model.user.vo.UserInfoVO;
import com.zdk.mai.common.security.annotation.NotRequiresLogin;
import com.zdk.mai.common.security.annotation.RateLimiter;
import com.zdk.mai.search.service.ISearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author zdk
 * @Date 2023/2/24 22:32
 */
@Api(tags = "搜索接口")
@Validated
@RestController
public class SearchController {

    @Autowired
    private ISearchService searchService;

    @Autowired
    private RemoteUserService remoteUserService;

    @Autowired
    @Qualifier("mailTaskExecutor")
    public Executor taskExecutor;


    @ApiOperation(value = "搜索贴子", notes = "搜索贴子")
    @AccessLog(title = "搜索贴子",businessType = BusinessType.SELECT)
    @NotRequiresLogin
    @RateLimiter(prefix = "search",key = "postList",expireTime = 1,maxCount = 2,rateMessage = "操作过于频繁")
    @GetMapping("/search/list/post")
    public CommonResult postList(@Valid SearchRequest searchRequest){

        EsPageInfo<EsPostModel> pageInfo = searchService.postPage(searchRequest);
        List<SearchPostVO> voList = voList(pageInfo);
        PageInfo<EsPostModel> page = new PageInfo<>();
        BeanUtils.copyProperties(pageInfo, page);
        return CommonResult.success(PageResponse.pageResponse(voList,page));
    }

    /**
     * 获取vo
     * @param pageInfo
     * @return
     */
    public List<SearchPostVO> voList(EsPageInfo<EsPostModel> pageInfo){
        List<SearchPostVO> voList = new ArrayList<>(pageInfo.getSize());

        List<CompletableFuture<CommonResult<UserInfoVO>>> resultsFutureList = new ArrayList<>(pageInfo.getSize());

        for (EsPostModel model : pageInfo.getList()) {
            resultsFutureList.add(CompletableFuture.supplyAsync(()-> remoteUserService.profile(model.getAuthorId()),taskExecutor));
        }

        List<CommonResult<UserInfoVO>> commonResults = resultsFutureList.stream().map(CompletableFuture::join).collect(Collectors.toList());

        for (int i = 0; i < pageInfo.getList().size(); i++) {
            EsPostModel model = pageInfo.getList().get(i);
            SearchPostVO vo = new SearchPostVO();
            BeanUtils.copyProperties(model, vo);

            UserInfoVO userInfoVO = commonResults.get(i).getData();
            vo.setUserInfoVO(userInfoVO);

            voList.add(vo);
        }
        return voList;
    }

    @ApiOperation(value = "搜索用户", notes = "搜索用户")
    @AccessLog(title = "搜索用户",businessType = BusinessType.SELECT)
    @NotRequiresLogin
    @RateLimiter(prefix = "search",key = "userList",expireTime = 1,maxCount = 2,rateMessage = "操作过于频繁")
    @GetMapping("/search/list/user")
    public CommonResult userList(@Valid SearchRequest searchRequest){

        EsPageInfo<EsUserModel> pageInfo = searchService.userPage(searchRequest);
        PageInfo<EsUserModel> page = new PageInfo<>();
        BeanUtils.copyProperties(pageInfo, page);

        return CommonResult.success(PageResponse.pageResponse(pageInfo.getList(), page));
    }




}
