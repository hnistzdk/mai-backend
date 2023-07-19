package com.zdk.mai.api.user.fallback;

import com.zdk.mai.api.user.RemoteUserService;
import com.zdk.mai.common.core.response.CommonResult;
import com.zdk.mai.common.model.user.request.UserCreateRequest;
import com.zdk.mai.common.model.user.vo.LoginUserVO;
import com.zdk.mai.common.model.user.vo.UserInfoVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/8 10:52
 */
@Component
public class RemoteUserFallbackFactory implements FallbackFactory<RemoteUserService> {

    private static final Logger log = LoggerFactory.getLogger(RemoteUserFallbackFactory.class);

    @Override
    public RemoteUserService create(Throwable cause) {
        log.error("用户服务调用失败:{}", cause.getMessage());
        return new RemoteUserService() {
            @Override
            public CommonResult<LoginUserVO> getUserInfo(String username, String source) {
                return CommonResult.fail("获取用户信息失败:"+cause.getMessage());
            }

            @Override
            public CommonResult<Long> create(UserCreateRequest userCreateRequest, String source) {
                return CommonResult.fail("创建用户失败:"+cause.getMessage());
            }

            @Override
            public String getAvatar(Long userId, String source) {
                return null;
            }

            @Override
            public CommonResult<UserInfoVO> profile(Long userId) {
                return CommonResult.fail("获取用户资料失败:"+cause.getMessage());
            }
        };
    }
}
