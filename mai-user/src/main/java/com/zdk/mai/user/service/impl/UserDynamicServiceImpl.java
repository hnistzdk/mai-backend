package com.zdk.mai.user.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zdk.mai.api.comment.RemoteCommentService;
import com.zdk.mai.api.post.RemotePostService;
import com.zdk.mai.common.core.constant.SecurityConstants;
import com.zdk.mai.common.core.enums.DynamicTypeEnum;
import com.zdk.mai.common.core.enums.PostTypeEnum;
import com.zdk.mai.common.core.response.CommonResult;
import com.zdk.mai.common.datasource.service.BaseServiceImpl;
import com.zdk.mai.common.model.comment.vo.CommentVO;
import com.zdk.mai.common.model.dynamic.UserDynamicModel;
import com.zdk.mai.common.model.dynamic.request.DynamicSearchRequest;
import com.zdk.mai.common.model.dynamic.vo.UserDynamicVO;
import com.zdk.mai.common.model.post.vo.PostDetailVO;
import com.zdk.mai.common.model.user.vo.UserInfoVO;
import com.zdk.mai.user.mapper.UserDynamicMapper;
import com.zdk.mai.user.service.IUserDynamicService;
import com.zdk.mai.user.service.IUserFollowService;
import com.zdk.mai.user.service.IUserService;
import org.jsoup.Jsoup;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/11 12:00
 */
@Service
public class UserDynamicServiceImpl extends BaseServiceImpl<UserDynamicMapper, UserDynamicModel> implements IUserDynamicService {

    @Autowired
    private IUserService userService;

    @Autowired
    private RemoteCommentService remoteCommentService;

    @Autowired
    private RemotePostService remotePostService;


    @Override
    public PageInfo<UserDynamicModel> list(DynamicSearchRequest searchRequest) {

        PageHelper.startPage(searchRequest.getCurrentPage(), searchRequest.getPageSize());
        List<UserDynamicModel> modelList = lambdaQuery()
                .eq(UserDynamicModel::getUserId, searchRequest.getUserId())
                .eq(UserDynamicModel::getState, true)
                .orderByDesc(UserDynamicModel::getUpdateTime)
                .list();
        return new PageInfo<>(modelList);

    }

    @Override
    public List<UserDynamicVO> buildVOList(PageInfo<UserDynamicModel> modelPageInfo, Long userId) {
        List<UserDynamicModel> modelList = modelPageInfo.getList();
        if (modelList.isEmpty()){
            return Collections.emptyList();
        }
        List<UserDynamicVO> voList = new ArrayList<>(modelList.size());
        for (UserDynamicModel dynamicModel : modelList) {
            UserDynamicVO vo = new UserDynamicVO();
            BeanUtils.copyProperties(dynamicModel, vo);
            voList.add(vo);
        }

        // 此用户的信息
        UserInfoVO userProfile = userService.profile(userId);

        for (UserDynamicVO dynamicVO : voList) {
            Long objectId = dynamicVO.getObjectId();
            Integer dynamicType = dynamicVO.getType();

            dynamicVO.setUsername(userProfile.getUsername());
            dynamicVO.setAvatar(userProfile.getAvatar());
            //关注相关
            if (dynamicType.equals(DynamicTypeEnum.FOLLOW.getType())){
                //这里的操作对象是被关注者的userId
                UserInfoVO toUserProfile = userService.profile(dynamicVO.getObjectId());
                dynamicVO.setTitle(toUserProfile.getUsername());
            }else {
                //以下的objectId都是postId
                CommonResult<List<PostDetailVO>> post = remotePostService.getByIds(Collections.singletonList(objectId), SecurityConstants.INNER);
                List<PostDetailVO> data = post.getData();
                if (CollectionUtil.isEmpty(data)){
                    continue;
                }
                PostDetailVO postDetailVO = data.get(0);
                if (postDetailVO == null){
                    continue;
                }
                //贴子相关(发帖、点赞贴子、评论贴子)
                if (dynamicType.equals(DynamicTypeEnum.POST.getType())
                        ||dynamicType.equals(DynamicTypeEnum.POST_LIKE.getType())
                        ||dynamicType.equals(DynamicTypeEnum.POST_COMMENT.getType())){
                    //普通贴子
                    if (postDetailVO.getType().equals(PostTypeEnum.POST.getType())){
                        dynamicVO.setTitle(postDetailVO.getTitle());
                    }
                    //职言
                    if (postDetailVO.getType().equals(PostTypeEnum.GOSSIP.getType())){
                        dynamicVO.setTitle(postDetailVO.getContent());
                    }
                }
                //评论相关(点赞评论、回复评论)
                if (dynamicType.equals(DynamicTypeEnum.COMMENT_LIKE.getType())
                        ||dynamicType.equals(DynamicTypeEnum.COMMENT_REPLY.getType())){
                    CommonResult<CommentVO> result = remoteCommentService.getById(dynamicVO.getCommentId(),SecurityConstants.INNER);
                    CommentVO commentVO = result.getData();
                    if (commentVO == null){
                        continue;
                    }
                    String content = commentVO.getContent();
                    if (postDetailVO.getType().equals(PostTypeEnum.POST.getType())){
                        dynamicVO.setTitle(postDetailVO.getTitle() + " > " + Jsoup.parse(content).text());
                    }
                    if (postDetailVO.getType().equals(PostTypeEnum.GOSSIP.getType())){
                        dynamicVO.setTitle(postDetailVO.getContent() + " > " + Jsoup.parse(content).text());
                    }
                }
            }
        }
        return voList;
    }


}
