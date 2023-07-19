package com.zdk.mai.comment.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zdk.mai.comment.mapper.CommentMapper;
import com.zdk.mai.comment.service.ICommentLikeService;
import com.zdk.mai.comment.service.ICommentService;
import com.zdk.mai.common.core.enums.DynamicTypeEnum;
import com.zdk.mai.common.core.enums.SortRuleEnum;
import com.zdk.mai.common.core.enums.message.MessageTypeEnum;
import com.zdk.mai.common.core.enums.message.NoticeTypeEnum;
import com.zdk.mai.common.core.enums.message.ObjectTypeEnum;
import com.zdk.mai.common.core.exception.BusinessException;
import com.zdk.mai.common.datasource.service.BaseServiceImpl;
import com.zdk.mai.common.model.comment.CommentModel;
import com.zdk.mai.common.model.comment.request.CommentCreateRequest;
import com.zdk.mai.common.model.comment.request.CommentDeleteRequest;
import com.zdk.mai.common.model.comment.request.CommentSearchRequest;
import com.zdk.mai.common.model.comment.vo.CommentVO;
import com.zdk.mai.common.model.dynamic.UserDynamicModel;
import com.zdk.mai.common.model.dynamic.request.DynamicDeleteRequest;
import com.zdk.mai.common.model.message.request.MessageCreateRequest;
import com.zdk.mai.common.model.message.request.MessageDeleteRequest;
import com.zdk.mai.common.rabbit.constant.RabbitConstants;
import com.zdk.mai.common.redis.service.RedisService;
import com.zdk.mai.common.security.enums.ErrorEnum;
import com.zdk.mai.common.security.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/2 19:34
 */
@Slf4j
@Service
public class CommentServiceImpl extends BaseServiceImpl<CommentMapper, CommentModel> implements ICommentService {

    @Autowired
    private ICommentLikeService commentLikeService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedisService redisService;


    @CacheEvict(cacheNames = "comment",key = "'comment_count:'+#createRequest.postId")
    @Override
    public void create(CommentCreateRequest createRequest) {
        CommentModel model = new CommentModel();
        BeanUtils.copyProperties(createRequest, model);
        model.setCreateUsername(SecurityUtils.getUsername());
        Long parentId = createRequest.getParentId();
        //填充贴子信息
        Long postId = createRequest.getPostId();
        Long authorId = createRequest.getPostUserId();
        String authorUsername = createRequest.getPostUsername();
        model.setPostId(postId);
        model.setPostUserId(authorId);
        model.setPostUsername(authorUsername);
        //不是0证明有父级评论 将父评论信息填充到此评论中
        if (parentId != 0) {
            //填充评论信息
            CommentModel parentComment = getById(parentId);
            model.setParentUserId(parentComment.getCreateBy());
            model.setParentUsername(parentComment.getCreateUsername());
        }
        baseMapper.insert(model);
        //parentId是0  那么就是对贴子的回复 rootId设为它自己的id
        if (parentId == 0) {
            model.setRootId(model.getCommentId());
            lambdaUpdate().eq(CommentModel::getCommentId, model.getCommentId())
                    .set(CommentModel::getRootId, model.getCommentId()).update();
        }
        //评论内容同步到es
        rabbitTemplate.convertAndSend(RabbitConstants.SYNC_DATA_EXCHANGE,RabbitConstants.SYNC_ES_ROUTING_KEY_COMMENT,model);

        //发消息更新到用户动态(评论的objectId也是postId)
        UserDynamicModel dynamicModel;
        if (parentId == 0){
            //对贴子的评论
            dynamicModel = new UserDynamicModel(null, createRequest.getCreateBy(), DynamicTypeEnum.POST_COMMENT.getType(), createRequest.getPostId(),model.getCommentId(),true);
        }else {
            //对评论的回复
            dynamicModel = new UserDynamicModel(null, createRequest.getCreateBy(), DynamicTypeEnum.COMMENT_REPLY.getType(), createRequest.getPostId(),model.getCommentId(),true);
        }
        dynamicModel = dynamicModel.build(createRequest);
        rabbitTemplate.convertAndSend(RabbitConstants.SYNC_DATA_EXCHANGE,RabbitConstants.SYNC_DB_ROUTING_KEY_USER,dynamicModel);

        //增加站内消息通知
        createMessage(model);

    }

    private void createMessage(CommentModel commentModel){
        //增加站内消息通知
        MessageCreateRequest messageCreateRequest = new MessageCreateRequest();
        messageCreateRequest.setCreateBy(commentModel.getCreateBy());
        messageCreateRequest.setCreateTime(new Date());
        messageCreateRequest.setUpdateBy(commentModel.getUpdateBy());
        messageCreateRequest.setUpdateTime(new Date());
        messageCreateRequest.setSenderId(commentModel.getCreateBy());
        // 父id为null则是回复贴子  否则则是回复评论
        if (commentModel.getParentUserId() == null){
            messageCreateRequest.setReceiveId(commentModel.getPostUserId());
            messageCreateRequest.setObjectType(ObjectTypeEnum.POST.getObjectType());
        }else {
            messageCreateRequest.setReceiveId(commentModel.getParentUserId());
            messageCreateRequest.setObjectType(ObjectTypeEnum.COMMENT.getObjectType());
        }
        messageCreateRequest.setParentCommentId(commentModel.getParentId());
        messageCreateRequest.setObjectId(commentModel.getPostId());
        messageCreateRequest.setCommentId(commentModel.getCommentId());
        messageCreateRequest.setContent(commentModel.getContent());
        messageCreateRequest.setSenderUsername(commentModel.getCreateUsername());
        messageCreateRequest.setSenderAvatar(redisService.getAvatar(commentModel.getCreateBy()));
        messageCreateRequest.setMessageType(MessageTypeEnum.REPLY.getMessageType());
        messageCreateRequest.setNoticeType(NoticeTypeEnum.SYSTEM_NOTICE.getNoticeType());

        rabbitTemplate.convertAndSend(RabbitConstants.SYNC_DATA_EXCHANGE,RabbitConstants.MESSAGE_DB_ROUTING_KEY, messageCreateRequest);

    }

    @Override
    public PageInfo<CommentModel> list(CommentSearchRequest searchRequest) {
        Long postId = searchRequest.getPostId();

        //先查出对贴子的评论
        PageHelper.startPage(searchRequest.getCurrentPage(), searchRequest.getPageSize());
        List<CommentModel> toPost = lambdaQuery()
                .eq(CommentModel::getPostId, postId)
                .eq(CommentModel::getParentId, 0)
                .orderByDesc(CommentModel::getUpdateTime)
                .list();
        return new PageInfo<>(toPost);
    }

    @Override
    public List<CommentVO> buildCommentVOList(List<CommentModel> toPost,String sortRule){
        List<CommentVO> voList = new ArrayList<>(toPost.size());
        for (CommentModel toPostModel : toPost) {
            int repliesCount = 0;
            CommentVO vo = new CommentVO();
            //设置评论者的头像
            String avatar = redisService.getAvatar(toPostModel.getCreateBy());
            vo.setAvatar(avatar);

            //设置点赞信息
            vo.setLike(commentLikeService.userLikeOrNot(toPostModel.getCommentId()));
            vo.setLikeCount(commentLikeService.getLikeCount(toPostModel.getCommentId()));

            //再查出对评论的评论  即parentId != 0的
            List<CommentModel> toComment = lambdaQuery()
                    .eq(CommentModel::getRootId, toPostModel.getCommentId())
                    .orderByDesc(CommentModel::getUpdateTime)
                    .ne(CommentModel::getParentId, 0)
                    .list();

            //统计回复数
            repliesCount += toComment.size();
            BeanUtils.copyProperties(toPostModel, vo);
            vo.setRepliesCount((long) repliesCount);
            List<CommentVO> toCommentVOList = new ArrayList<>(toComment.size());
            for (CommentModel commentModel : toComment) {
                CommentVO toCommentVO = new CommentVO();
                BeanUtils.copyProperties(commentModel, toCommentVO);
                //设置评论者的头像
                String avatar1 = redisService.getAvatar(commentModel.getCreateBy());
                toCommentVO.setAvatar(avatar1);

                //设置点赞信息
                toCommentVO.setLike(commentLikeService.userLikeOrNot(commentModel.getCommentId()));
                toCommentVO.setLikeCount(commentLikeService.getLikeCount(commentModel.getCommentId()));

                toCommentVOList.add(toCommentVO);
            }
            vo.setToComment(toCommentVOList);
            voList.add(vo);
        }
        //排序
        if (sortRule.equals(SortRuleEnum.HOTTEST.getName())) {
            voList = voList.stream()
                    .sorted(Comparator.comparing(CommentVO::getLikeCount, Comparator.nullsLast(Comparator.reverseOrder()))
                            .thenComparing(CommentVO::getRepliesCount, Comparator.nullsLast(Comparator.reverseOrder()))
                            .thenComparing(CommentVO::getUpdateTime, Comparator.nullsLast(Comparator.reverseOrder())))
                    .collect(Collectors.toList());
        } else {
            voList = voList.stream()
                    .sorted(Comparator.comparing(CommentVO::getUpdateTime, Comparator.nullsLast(Comparator.reverseOrder())))
                    .collect(Collectors.toList());
        }
        return voList;
    }

    @Caching(evict = {@CacheEvict(cacheNames = "comment",key = "'comment_count:'+#postId")
    ,@CacheEvict(cacheNames = "comment",key = "'detail:'+#commentId")})
    @Override
    public void delete(Long commentId,Long postId) {

        CommentVO comment = getById(commentId);

        if (!comment.getCreateBy().equals(SecurityUtils.getUserId())){
            throw new BusinessException(ErrorEnum.UNAUTHORIZED_MODIFICATION.getCode(), ErrorEnum.UNAUTHORIZED_MODIFICATION.getMsg());
        }

        // 先删commentId自己 且要把rootId为此commentId的评论都删掉
        lambdaUpdate()
                .set(CommentModel::getDelFlag, true)
                .eq(CommentModel::getCommentId, commentId)
                .or()
                .eq(CommentModel::getRootId, commentId)
                .update();

        //删es
        CommentDeleteRequest commentDeleteRequest = new CommentDeleteRequest();
        commentDeleteRequest.setPostId(postId);
        commentDeleteRequest.setCommentId(commentId);
        rabbitTemplate.convertAndSend(RabbitConstants.SYNC_DATA_EXCHANGE, RabbitConstants.SYNC_ES_ROUTING_KEY_COMMENT, commentDeleteRequest);

        //删动态(删除时不用管type)
        DynamicDeleteRequest dynamicDeleteRequest = new DynamicDeleteRequest();
        dynamicDeleteRequest.setObjectId(postId);
        dynamicDeleteRequest.setCommentId(commentId);
        dynamicDeleteRequest.setUpdateTime(new Date());
        rabbitTemplate.convertAndSend(RabbitConstants.SYNC_DATA_EXCHANGE, RabbitConstants.SYNC_DB_ROUTING_KEY_USER, dynamicDeleteRequest);


        //删除消息
        MessageDeleteRequest messageDeleteRequest = new MessageDeleteRequest();
        messageDeleteRequest.setObjectId(postId);
        messageDeleteRequest.setCommentId(commentId);
        rabbitTemplate.convertAndSend(RabbitConstants.SYNC_DATA_EXCHANGE,RabbitConstants.MESSAGE_DB_ROUTING_KEY, messageDeleteRequest);
    }

    @Cacheable(cacheNames = "comment",key = "'detail:'+#commentId")
    @Override
    public CommentVO getById(Long commentId) {
        CommentModel commentModel = baseMapper.selectById(commentId);
        CommentVO vo = new CommentVO();
        BeanUtils.copyProperties(commentModel, vo);
        return vo;
    }
}
