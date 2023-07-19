package com.zdk.mai.post.controller;

import com.github.pagehelper.PageInfo;
import com.zdk.mai.api.comment.RemoteCommentService;
import com.zdk.mai.api.user.RemoteUserService;
import com.zdk.mai.common.core.constant.SecurityConstants;
import com.zdk.mai.common.core.response.CommonResult;
import com.zdk.mai.common.core.response.PageResponse;
import com.zdk.mai.common.log.annotation.AccessLog;
import com.zdk.mai.common.log.enums.BusinessType;
import com.zdk.mai.common.model.post.PostModel;
import com.zdk.mai.common.model.post.request.PostCreateRequest;
import com.zdk.mai.common.model.post.request.PostSearchRequest;
import com.zdk.mai.common.model.post.request.PostUpdateRequest;
import com.zdk.mai.common.model.post.vo.PostDetailVO;
import com.zdk.mai.common.model.post.vo.PostStatisticalVO;
import com.zdk.mai.common.model.user.vo.UserAchievementVO;
import com.zdk.mai.common.model.user.vo.UserInfoVO;
import com.zdk.mai.common.rabbit.constant.RabbitConstants;
import com.zdk.mai.common.redis.service.RedisService;
import com.zdk.mai.common.security.annotation.*;
import com.zdk.mai.post.service.IPostLikeService;
import com.zdk.mai.post.service.IPostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * @Description 贴子控制器
 * @Author zdk
 * @Date 2023/2/28 17:26
 */
@Api(tags = "贴子服务接口")
@Slf4j
@RestController
@Validated
public class PostController {


    @Autowired
    private IPostService postService;

    @Autowired
    private IPostLikeService postLikeService;


    @Autowired
    private RemoteCommentService remoteCommentService;

    @Autowired
    private RemoteUserService remoteUserService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedisService redisService;


    @Autowired
    @Qualifier("mailTaskExecutor")
    public Executor taskExecutor;


    @ApiOperation(value = "发布贴子", notes = "发布贴子")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "createRequest", value = "createRequest", required = true, dataType = "PostCreateRequest"),
    })
    @InitRequestArg(argType = ArgType.CREATE)
    @RateLimiter(prefix = "post",key = "create",expireTime = 1,maxCount = 1,rateMessage = "操作过于频繁")
    @PostMapping("/post/create")
    public CommonResult create(@RequestBody @Valid PostCreateRequest createRequest){
        postService.create(createRequest);
        return CommonResult.success();
    }

    @ApiOperation(value = "更新贴子信息", notes = "更新贴子信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "updateRequest", value = "updateRequest", required = true, dataType = "PostUpdateRequest"),
    })
    @InitRequestArg(argType = ArgType.UPDATE)
    @RateLimiter(prefix = "post",key = "update",expireTime = 1,maxCount = 1,rateMessage = "操作过于频繁")
    @PostMapping("/post/update")
    public CommonResult update(@RequestBody @Valid PostUpdateRequest updateRequest){
        postService.update(updateRequest);
        return CommonResult.success();
    }


    @ApiOperation(value = "贴子详情", notes = "贴子详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "Long",paramType = "path"),
    })
    @NotRequiresLogin
    @GetMapping("/post/detail/{id}")
    public CommonResult<PostDetailVO> detail(@PathVariable("id") Long id){
        //异步更新库中pv
        rabbitTemplate.convertAndSend(RabbitConstants.SYNC_DATA_EXCHANGE,RabbitConstants.SYNC_DB_ROUTING_KEY_POST,id);
        PostDetailVO vo = postService.detail(id);
        return CommonResult.success(vo);
    }


    @ApiOperation(value = "贴子分页", notes = "贴子分页")
    @AccessLog(title = "贴子分页",businessType = BusinessType.SELECT)
    @NotRequiresLogin
    @GetMapping("/post/list")
    public CommonResult list(PostSearchRequest searchRequest){
        PageInfo<PostModel> pageInfo = postService.list(searchRequest);
        List<PostDetailVO> voList = getVOList(pageInfo);
        return CommonResult.success(PageResponse.pageResponse(voList, pageInfo));
    }

    @ApiOperation(value = "全站热榜", notes = "全站热榜")
    @AccessLog(title = "全站热榜",businessType = BusinessType.SELECT)
    @NotRequiresLogin
    @GetMapping("/post/list/hot")
    public CommonResult hotList(){
        List<PostModel> list = postService.lambdaQuery()
                .select(PostModel::getPostId,PostModel::getTitle,PostModel::getPv)
                .isNotNull(PostModel::getTitle)
                .orderByDesc(PostModel::getPv)
                .last("limit 10")
                .list();
        List<PostDetailVO> voList = new ArrayList<>(list.size());
        for (PostModel model : list) {
            PostDetailVO vo = new PostDetailVO();
            BeanUtils.copyProperties(model, vo);
            voList.add(vo);
        }
        return CommonResult.success(voList);
    }

    @ApiOperation(value = "点赞过的贴子", notes = "点赞过的贴子")
    @NotRequiresLogin
    @GetMapping("/post/list/like")
    public CommonResult likeList(PostSearchRequest searchRequest){
        PageInfo<PostModel> pageInfo = postService.likeList(searchRequest);
        List<PostDetailVO> voList = getVOList(pageInfo);
        return CommonResult.success(PageResponse.pageResponse(voList, pageInfo));
    }

    @ApiOperation(value = "贴子统计数据", notes = "贴子统计数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "postId", value = "postId", required = true, dataType = "Long"),
    })
    @NotRequiresLogin
    @GetMapping("/post/statistical")
    public CommonResult<PostStatisticalVO> statistical(@RequestParam("postId") @NotNull Long postId){
        return CommonResult.success(postService.statistical(postId));
    }

    @ApiOperation(value = "用户的贴子相关统计数据", notes = "用户的贴子相关统计数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "userId", required = true, dataType = "Long",paramType = "path"),
    })
    @InnerAuth
    @GetMapping("/post/userAchievement/{userId}")
    public CommonResult<UserAchievementVO> userAchievement(@PathVariable("userId") Long userId){
        UserAchievementVO vo = postService.userAchievement(userId);
        return CommonResult.success(vo);
    }

    @ApiOperation(value = "id批量获取贴子", notes = "id批量获取贴子")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "ids", required = true, dataType = "List"),
    })
    @InnerAuth
    @GetMapping("/post/getByIds")
    public CommonResult<List<PostDetailVO>> getByIds(@RequestParam("ids") List<Long> ids){

        return CommonResult.success(postService.getByIds(ids));

    }

    @ApiOperation(value = "删除贴子", notes = "删除贴子")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "postId", value = "postId", required = true, dataType = "Long",paramType = "path"),
    })
    @PostMapping("/post/delete/{postId}")
    public CommonResult<List<PostDetailVO>> delete(@PathVariable("postId") Long postId){
        postService.delete(postId);

        return CommonResult.success();

    }

    /**
     * 构造vo
     * @param pageInfo
     * @return
     */
    @SneakyThrows
    private List<PostDetailVO> getVOList(PageInfo<PostModel> pageInfo){
        List<PostDetailVO> voList = new ArrayList<>(pageInfo.getSize());
        for (PostModel model : pageInfo.getList()) {
            Long postId = model.getPostId();
            PostDetailVO vo = new PostDetailVO();
            BeanUtils.copyProperties(model, vo);
            if (!redisService.hasKey("user::profile:" + model.getAuthorId())){
                CompletableFuture<UserInfoVO> userFuture = CompletableFuture.supplyAsync(() ->
                        remoteUserService.profile(model.getAuthorId()).getData(), taskExecutor);
                CompletableFuture<Integer> commentFuture = CompletableFuture.supplyAsync(() ->
                        remoteCommentService.postCommentCount(postId, SecurityConstants.INNER), taskExecutor);

                CompletableFuture.allOf(userFuture,commentFuture).join();

                vo.setUserInfoVO(userFuture.get());
                //设置评论数信息
                vo.setCommentCount(commentFuture.get().longValue());
            }else {
                UserInfoVO userInfoVO = redisService.getCacheObject("user::profile:" + model.getAuthorId());
                vo.setUserInfoVO(userInfoVO);
                vo.setCommentCount(remoteCommentService.postCommentCount(postId, SecurityConstants.INNER).longValue());
            }
            //点赞相关信息设置
            vo.setLike(postLikeService.userLikeOrNot(postId));
            vo.setLikeCount(postLikeService.getLikeCount(postId));

            voList.add(vo);
        }
        return voList;
    }


    @ApiOperation(value = "置顶", notes = "置顶")
    @PostMapping("/post/top")
    public CommonResult top(@RequestBody PostUpdateRequest updateRequest){

        Long postId = updateRequest.getPostId();
        Boolean top = updateRequest.getTop();
        postService.lambdaUpdate()
                .eq(PostModel::getPostId, postId)
                .set(PostModel::getTop, top)
                .update();

        return CommonResult.success();
    }
}
