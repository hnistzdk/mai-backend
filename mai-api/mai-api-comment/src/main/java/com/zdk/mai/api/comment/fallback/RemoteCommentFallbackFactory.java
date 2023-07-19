package com.zdk.mai.api.comment.fallback;

import com.zdk.mai.api.comment.RemoteCommentService;
import com.zdk.mai.common.core.response.CommonResult;
import com.zdk.mai.common.model.comment.vo.CommentVO;
import com.zdk.mai.common.model.user.vo.UserAchievementVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/8 11:01
 */
@Component
public class RemoteCommentFallbackFactory implements FallbackFactory<RemoteCommentService> {

    private static final Logger log = LoggerFactory.getLogger(RemoteCommentFallbackFactory.class);

    @Override
    public RemoteCommentService create(Throwable cause) {

        log.error("评论服务调用失败:{}", cause.getMessage());

        return new RemoteCommentService() {
            @Override
            public Integer postCommentCount(Long postId, String source) {
                return 0;
            }

            @Override
            public CommonResult<UserAchievementVO> userAchievement(Long userId, String source) {
                return CommonResult.fail("获取用户评论成就信息失败:"+cause.getMessage());
            }

            @Override
            public CommonResult<CommentVO> getById(Long commentId, String source) {
                return CommonResult.fail("获取评论信息失败:"+cause.getMessage());
            }
        };
    }
}
