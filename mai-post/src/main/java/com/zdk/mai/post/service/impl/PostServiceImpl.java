package com.zdk.mai.post.service.impl;

import cn.hutool.http.HtmlUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zdk.mai.api.comment.RemoteCommentService;
import com.zdk.mai.common.core.constant.SecurityConstants;
import com.zdk.mai.common.core.enums.DynamicTypeEnum;
import com.zdk.mai.common.core.enums.message.MessageTypeEnum;
import com.zdk.mai.common.core.enums.message.ObjectTypeEnum;
import com.zdk.mai.common.core.exception.BusinessException;
import com.zdk.mai.common.datasource.service.BaseServiceImpl;
import com.zdk.mai.common.model.dynamic.UserDynamicModel;
import com.zdk.mai.common.model.dynamic.request.DynamicDeleteRequest;
import com.zdk.mai.common.model.message.request.MessageDeleteRequest;
import com.zdk.mai.common.model.post.PostModel;
import com.zdk.mai.common.model.post.request.PostCreateRequest;
import com.zdk.mai.common.model.post.request.PostDeleteRequest;
import com.zdk.mai.common.model.post.request.PostSearchRequest;
import com.zdk.mai.common.model.post.request.PostUpdateRequest;
import com.zdk.mai.common.model.post.vo.PostDetailVO;
import com.zdk.mai.common.model.post.vo.PostStatisticalVO;
import com.zdk.mai.common.model.user.vo.UserAchievementVO;
import com.zdk.mai.common.rabbit.constant.RabbitConstants;
import com.zdk.mai.common.redis.service.RedisService;
import com.zdk.mai.common.security.auth.AuthUtil;
import com.zdk.mai.common.security.enums.ErrorEnum;
import com.zdk.mai.common.security.utils.SecurityUtils;
import com.zdk.mai.post.mapper.PostMapper;
import com.zdk.mai.post.service.IPostLikeService;
import com.zdk.mai.post.service.IPostService;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * @Description
 * @Author zdk
 * @Date 2023/2/28 17:28
 */
@Service
public class PostServiceImpl extends BaseServiceImpl<PostMapper, PostModel> implements IPostService {


    @Autowired
    private RemoteCommentService remoteCommentService;

    @Autowired
    private IPostLikeService postLikeService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedisService redisService;

    @Autowired
    @Qualifier("mailTaskExecutor")
    public Executor taskExecutor;


    @Override
    public void create(PostCreateRequest createRequest) {
        PostModel model = new PostModel();
        BeanUtils.copyProperties(createRequest, model);
        String html = HtmlUtil.filter(createRequest.getHtml());
        model.setContent(Jsoup.parse(html).text());
        model.setAuthorId(createRequest.getCreateBy());
        model.setAuthorUsername(SecurityUtils.getUsername());
        model.setImages(StringUtils.join(createRequest.getImages(), ","));
        //插入db
        baseMapper.insert(model);

        //发送消息插入到es
        rabbitTemplate.convertAndSend(RabbitConstants.SYNC_DATA_EXCHANGE,RabbitConstants.SYNC_ES_ROUTING_KEY_POST,model);

        //发消息更新到用户动态
        UserDynamicModel dynamicModel = new UserDynamicModel(null, createRequest.getCreateBy(), DynamicTypeEnum.POST.getType(), model.getPostId(),null,true);
        dynamicModel = dynamicModel.build(createRequest);
        rabbitTemplate.convertAndSend(RabbitConstants.SYNC_DATA_EXCHANGE,RabbitConstants.SYNC_DB_ROUTING_KEY_USER,dynamicModel);

    }

    @SuppressWarnings({"unchecked"})
    @Override
    public PageInfo<PostModel> list(PostSearchRequest searchRequest) {

        String keywords = searchRequest.getKeywords();
        Long userId = searchRequest.getUserId();
        // 是否在个人主页tab下
        Boolean isTab = searchRequest.getIsTab();
        isTab = isTab != null && isTab;

        boolean admin = AuthUtil.isAdmin();

        PageHelper.startPage(searchRequest.getCurrentPage(), searchRequest.getPageSize());
        List<PostModel> list = lambdaQuery()
                //不是管理员且传了userId才查传入的人的;或者是当前是管理员,但是是在个人主页的tab下,只查此用户的
                .eq((isOk(userId) && !admin) || (isOk(userId) && admin) && isTab, PostModel::getAuthorId,userId)
                .eq(PostModel::getType,searchRequest.getType())
                .and(isOk(keywords),
                        e->e.like(isOk(keywords), PostModel::getTitle, keywords)
                        .or()
                        .like(isOk(keywords), PostModel::getContent, keywords))
                .orderByDesc(PostModel::getTop,PostModel::getUpdateTime)
                .list();
        return new PageInfo<>(list);
    }

    @SneakyThrows
    @Override
    public PostStatisticalVO statistical(Long postId) {
        PostStatisticalVO vo = new PostStatisticalVO();
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> remoteCommentService.postCommentCount(postId, SecurityConstants.INNER), taskExecutor);
        //点赞相关信息设置
        vo.setLike(postLikeService.userLikeOrNot(postId));
        vo.setLikeCount(postLikeService.getLikeCount(postId));
        long commentCount = future.get().longValue();
        vo.setCommentCount(commentCount);
        return vo;
    }

    @Override
    public UserAchievementVO userAchievement(Long userId) {
        //贴子总获赞数
        Long postLikeCount = postLikeService.getUserPostLikeCount(userId).longValue();
        //贴子总pv
        Long userPostPv = getUserPostPv(userId);
        userPostPv = userPostPv == null ? 0 : userPostPv;
        UserAchievementVO vo = new UserAchievementVO();
        vo.setPostLikeCount(postLikeCount);
        vo.setPostReadCount(userPostPv);
        return vo;
    }

    /**
     * 用户所有文章的pv
     * @param userId
     * @return
     */
    @Cacheable(cacheNames = {"post"},key = "'user_post_pv:'+#userId")
    public Long getUserPostPv(Long userId){
        return baseMapper.getUserPostPv(userId);
    }

    @Override
    public PostDetailVO detail(Long postId) {
        PostModel postModel = getById(postId);
        PostDetailVO vo = new PostDetailVO();
        BeanUtils.copyProperties(postModel, vo);
        String avatar = redisService.getAvatar(postModel.getAuthorId());
        vo.setAvatar(avatar);
        return vo;
    }

    @Override
    public List<PostDetailVO> getByIds(List<Long> ids) {
        List<PostModel> postModels = lambdaQuery().in(PostModel::getPostId, ids).list();
        List<PostDetailVO>  voList = new ArrayList<>(postModels.size());
        for (PostModel postModel : postModels) {
            PostDetailVO vo = new PostDetailVO();
            BeanUtils.copyProperties(postModel, vo);
            voList.add(vo);
        }
        return voList;
    }

    @CacheEvict(cacheNames = {"post"},key = "'detail:'+ #postId")
    @Override
    public void delete(Long postId) {
        PostDetailVO detail = this.detail(postId);
        if (!AuthUtil.isAdmin() && !detail.getAuthorId().equals(SecurityUtils.getUserId())){
            throw new BusinessException(ErrorEnum.UNAUTHORIZED_MODIFICATION.getCode(), ErrorEnum.UNAUTHORIZED_MODIFICATION.getMsg());
        }
        baseMapper.deleteById(postId);

        //删es
        PostDeleteRequest deleteRequest = new PostDeleteRequest();
        deleteRequest.setPostId(postId);
        rabbitTemplate.convertAndSend(RabbitConstants.SYNC_DATA_EXCHANGE, RabbitConstants.SYNC_ES_ROUTING_KEY_POST, deleteRequest);

        //删动态(删除时不用管type)
        DynamicDeleteRequest dynamicDeleteRequest = new DynamicDeleteRequest();
        dynamicDeleteRequest.setObjectId(postId);
        dynamicDeleteRequest.setUpdateTime(new Date());
        rabbitTemplate.convertAndSend(RabbitConstants.SYNC_DATA_EXCHANGE, RabbitConstants.SYNC_DB_ROUTING_KEY_USER, dynamicDeleteRequest);

        //删除消息
        MessageDeleteRequest messageDeleteRequest = new MessageDeleteRequest();
        messageDeleteRequest.setObjectType(ObjectTypeEnum.POST.getObjectType());
        messageDeleteRequest.setObjectId(postId);
        rabbitTemplate.convertAndSend(RabbitConstants.SYNC_DATA_EXCHANGE,RabbitConstants.MESSAGE_DB_ROUTING_KEY, messageDeleteRequest);
    }

    @Override
    public PageInfo<PostModel> likeList(PostSearchRequest searchRequest) {
        PageHelper.startPage(searchRequest.getCurrentPage(), searchRequest.getPageSize());
        List<PostModel> list = baseMapper.likeList(searchRequest.getUserId());
        return new PageInfo<>(list);
    }


    @Override
    public void update(PostUpdateRequest updateRequest) {

        Long postId = updateRequest.getPostId();
        PostModel model = getById(postId);
        if (!AuthUtil.isAdmin() && !model.getAuthorId().equals(updateRequest.getUpdateBy())){
            throw new BusinessException(ErrorEnum.UNAUTHORIZED_MODIFICATION.getCode(), ErrorEnum.UNAUTHORIZED_MODIFICATION.getMsg());
        }

        BeanUtils.copyProperties(updateRequest, model);
        String html = HtmlUtil.filter(updateRequest.getHtml());
        model.setContent(Jsoup.parse(html).text());
        model.setImages(StringUtils.join(updateRequest.getImages(), ","));
        baseMapper.updateById(model);

        //发送消息更新到es
        rabbitTemplate.convertAndSend(RabbitConstants.SYNC_DATA_EXCHANGE,RabbitConstants.SYNC_ES_ROUTING_KEY_POST, model);

    }
}
