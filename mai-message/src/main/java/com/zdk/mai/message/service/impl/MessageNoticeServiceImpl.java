package com.zdk.mai.message.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zdk.mai.api.comment.RemoteCommentService;
import com.zdk.mai.api.post.RemotePostService;
import com.zdk.mai.common.core.constant.CacheConstants;
import com.zdk.mai.common.core.constant.SecurityConstants;
import com.zdk.mai.common.core.enums.message.MessageTypeEnum;
import com.zdk.mai.common.core.exception.BusinessException;
import com.zdk.mai.common.datasource.service.BaseServiceImpl;
import com.zdk.mai.common.model.comment.vo.CommentVO;
import com.zdk.mai.common.model.message.MessageInfoModel;
import com.zdk.mai.common.model.message.MessageNoticeModel;
import com.zdk.mai.common.model.message.request.MessageSearchRequest;
import com.zdk.mai.common.model.message.request.MessageUpdateRequest;
import com.zdk.mai.common.model.message.vo.MessageVO;
import com.zdk.mai.common.model.post.vo.PostDetailVO;
import com.zdk.mai.common.redis.service.RedisService;
import com.zdk.mai.common.security.enums.ErrorEnum;
import com.zdk.mai.common.security.utils.SecurityUtils;
import com.zdk.mai.message.mapper.MessageNoticeMapper;
import com.zdk.mai.message.service.IMessageInfoService;
import com.zdk.mai.message.service.IMessageNoticeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/27 17:34
 */
@Service
public class MessageNoticeServiceImpl extends BaseServiceImpl<MessageNoticeMapper, MessageNoticeModel> implements IMessageNoticeService {

    @Autowired
    private IMessageInfoService messageInfoService;

    @Autowired
    private RemotePostService remotePostService;

    @Autowired
    private RemoteCommentService remoteCommentService;

    @Autowired
    private RedisService redisService;

    @Override
    public PageInfo<MessageNoticeModel> list(MessageSearchRequest searchRequest) {

        Long receiveId = SecurityUtils.getUserId();
        Integer noticeType = searchRequest.getNoticeType();
        Integer messageType = searchRequest.getMessageType();

        PageHelper.startPage(searchRequest.getCurrentPage(), searchRequest.getPageSize());
        List<MessageNoticeModel> list = lambdaQuery().eq(MessageNoticeModel::getReceiveId, receiveId)
                .eq(MessageNoticeModel::getNoticeType, noticeType)
                .eq(MessageNoticeModel::getMessageType, messageType)
                .orderByDesc(MessageNoticeModel::getCreateTime)
                .list();
        return new PageInfo<>(list);
    }

    @Override
    public List<MessageVO> voList(PageInfo<MessageNoticeModel> pageInfo, Integer noticeType) {

        List<MessageNoticeModel> messageNoticeList = pageInfo.getList();
        if (CollectionUtil.isEmpty(messageNoticeList)) {
            return Collections.emptyList();
        }
        List<Long> messageIds = messageNoticeList.stream().map(MessageNoticeModel::getMessageId).collect(Collectors.toList());

        //拿到所有消息(这里也要保证上面的排序顺序,否则vo信息会乱)
        List<MessageInfoModel> messageInfoList = messageInfoService.lambdaQuery()
                .in(MessageInfoModel::getMessageId, messageIds)
                .orderByDesc(MessageInfoModel::getCreateTime)
                .list();

        List<MessageVO> voList = new ArrayList<>(messageInfoList.size());

        for (int i = 0; i < messageInfoList.size(); i++) {
            MessageInfoModel messageInfo = messageInfoList.get(i);
            MessageNoticeModel messageNotice = messageNoticeList.get(i);
            MessageVO vo = new MessageVO();
            BeanUtils.copyProperties(messageInfo, vo);
            BeanUtils.copyProperties(messageNotice, vo);
            buildVO(vo);
            String senderAvatar = redisService.getAvatar(vo.getSenderId());
            vo.setSenderAvatar(senderAvatar);
            voList.add(vo);
        }
        return voList;
    }

    /**
     * 填充其他信息
     *
     * @param vo
     */
    private void buildVO(MessageVO vo) {
        Integer messageType = vo.getMessageType();
        if (messageType.equals(MessageTypeEnum.FOLLOW.getMessageType())) {
            //关注消息
            System.out.println("关注消息暂时不管");
        } else if (messageType.equals(MessageTypeEnum.SYSTEM.getMessageType())) {
            System.out.println("系统消息暂时不管");
        } else if (messageType.equals(MessageTypeEnum.LIKE.getMessageType())) {
            //点赞消息
            PostDetailVO postDetailVO = remotePostService.detail(vo.getObjectId()).getData();
            vo.setPostDetailVO(postDetailVO);
        } else {
            // 贴子、评论相关消息
            PostDetailVO postDetailVO = remotePostService.detail(vo.getObjectId()).getData();
            vo.setPostDetailVO(postDetailVO);
            if (isOk(vo.getParentCommentId())) {
                CommentVO commentVO = remoteCommentService.getById(vo.getParentCommentId(), SecurityConstants.INNER).getData();
                vo.setCommentVO(commentVO);
            }
        }
    }

    @Override
    public void markRead(MessageUpdateRequest messageUpdateRequest) {

        Long noticeId = messageUpdateRequest.getNoticeId();
        //当前的状态
        Boolean nowFlag = messageUpdateRequest.getReadFlag();

        Long userId = messageUpdateRequest.getUserId();

        if (!userId.equals(SecurityUtils.getUserId())) {
            throw new BusinessException(ErrorEnum.UNAUTHORIZED_MODIFICATION.getCode(), ErrorEnum.UNAUTHORIZED_MODIFICATION.getMsg());
        }

        lambdaUpdate()
                .eq(MessageNoticeModel::getNoticeId, noticeId)
                .set(MessageNoticeModel::getReadFlag, !nowFlag)
                .update();

        //更新未读消息数缓存
        //当前是已读,则修改后未读+1 否则未读-1
        int value = nowFlag ? 1 : -1;
        Integer messageType = messageUpdateRequest.getMessageType();
        if (messageType.equals(MessageTypeEnum.REPLY.getMessageType())) {
            redisService.incr(CacheConstants.MESSAGE_NOT_READ_COUNT_REPLY + userId, value);
        } else if (messageType.equals(MessageTypeEnum.LIKE.getMessageType())) {
            redisService.incr(CacheConstants.MESSAGE_NOT_READ_COUNT_LIKE + userId, value);
        } else if (messageType.equals(MessageTypeEnum.FOLLOW.getMessageType())) {
            redisService.incr(CacheConstants.MESSAGE_NOT_READ_COUNT_FOLLOW + userId, value);
        } else if (messageType.equals(MessageTypeEnum.SYSTEM.getMessageType())) {
            redisService.incr(CacheConstants.MESSAGE_NOT_READ_COUNT_SYSTEM + userId, value);
        }
        redisService.incr(CacheConstants.MESSAGE_NOT_READ_COUNT_ALL + userId, value);

    }

    @Override
    public void allRead(MessageUpdateRequest messageUpdateRequest) {
        Long userId = messageUpdateRequest.getUserId();
        Integer messageType = messageUpdateRequest.getMessageType();
        Integer noticeType = messageUpdateRequest.getNoticeType();

        if (!userId.equals(SecurityUtils.getUserId())) {
            throw new BusinessException(ErrorEnum.UNAUTHORIZED_MODIFICATION.getCode(), ErrorEnum.UNAUTHORIZED_MODIFICATION.getMsg());
        }

        if (notOk(messageType) || notOk(noticeType)) {
            throw new BusinessException(ErrorEnum.BAD_REQUEST.getCode(), ErrorEnum.BAD_REQUEST.getMsg());
        }

        baseMapper.allRead(messageUpdateRequest);

        // 更新未读消息数缓存
        int count = 0;
        if (messageType.equals(MessageTypeEnum.REPLY.getMessageType())) {
            count = redisService.getCacheObject(CacheConstants.MESSAGE_NOT_READ_COUNT_REPLY + userId);
            redisService.setCacheObject(CacheConstants.MESSAGE_NOT_READ_COUNT_REPLY + userId, 0);
        } else if (messageType.equals(MessageTypeEnum.LIKE.getMessageType())) {
            count = redisService.getCacheObject(CacheConstants.MESSAGE_NOT_READ_COUNT_LIKE + userId);
            redisService.setCacheObject(CacheConstants.MESSAGE_NOT_READ_COUNT_LIKE + userId, 0);
        } else if (messageType.equals(MessageTypeEnum.FOLLOW.getMessageType())) {
            count = redisService.getCacheObject(CacheConstants.MESSAGE_NOT_READ_COUNT_FOLLOW + userId);
            redisService.setCacheObject(CacheConstants.MESSAGE_NOT_READ_COUNT_FOLLOW + userId, 0);
        } else if (messageType.equals(MessageTypeEnum.SYSTEM.getMessageType())) {
            count = redisService.getCacheObject(CacheConstants.MESSAGE_NOT_READ_COUNT_SYSTEM + userId);
            redisService.setCacheObject(CacheConstants.MESSAGE_NOT_READ_COUNT_SYSTEM + userId, 0);
        }
        redisService.decr(CacheConstants.MESSAGE_NOT_READ_COUNT_ALL + userId, count);

    }

    @Override
    public Integer getNotReadMessageCount(Integer noticeType, Integer messageType) {
        return baseMapper.getNotReadMessageCount(noticeType, messageType, SecurityUtils.getUserId());
    }

}
