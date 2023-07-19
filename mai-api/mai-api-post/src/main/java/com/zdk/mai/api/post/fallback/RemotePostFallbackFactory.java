package com.zdk.mai.api.post.fallback;

import com.zdk.mai.api.post.RemotePostService;
import com.zdk.mai.common.core.response.CommonResult;
import com.zdk.mai.common.model.post.vo.PostDetailVO;
import com.zdk.mai.common.model.user.vo.UserAchievementVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/8 10:57
 */
@Component
public class RemotePostFallbackFactory implements FallbackFactory<RemotePostService> {

    private static final Logger log = LoggerFactory.getLogger(RemotePostFallbackFactory.class);

    @Override
    public RemotePostService create(Throwable cause) {
        log.error("贴子服务调用失败:{}", cause.getMessage());
        return new RemotePostService() {
            @Override
            public CommonResult<PostDetailVO> detail(Long id) {
                return CommonResult.fail("获取贴子详情信息失败:"+cause.getMessage());
            }

            @Override
            public CommonResult<UserAchievementVO> userAchievement(Long userId, String source) {
                return CommonResult.fail("获取用户贴子成就信息失败:"+cause.getMessage());
            }

            @Override
            public CommonResult<List<PostDetailVO>> getByIds(List<Long> ids, String source) {
                return CommonResult.fail("获取贴子信息失败:"+cause.getMessage());
            }
        };
    }
}
