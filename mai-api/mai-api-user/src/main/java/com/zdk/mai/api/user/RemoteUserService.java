package com.zdk.mai.api.user;

import com.zdk.mai.common.core.constant.SecurityConstants;
import com.zdk.mai.common.core.constant.ServiceNameConstants;
import com.zdk.mai.common.core.response.CommonResult;
import com.zdk.mai.common.model.user.request.UserCreateRequest;
import com.zdk.mai.common.model.user.vo.LoginUserVO;
import com.zdk.mai.common.model.user.vo.UserInfoVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @Description 用户服务暴露接口
 * @Author zdk
 * @Date 2022/11/27 17:56
 */
@FeignClient(value = ServiceNameConstants.USER_SERVICE)
public interface RemoteUserService {
    /**
     * 通过用户名查询用户信息(登录用)
     * @param username
     * @param source 访问来源
     * @return
     */
    @GetMapping("/user/info/{username}")
    CommonResult<LoginUserVO> getUserInfo(@PathVariable("username") String username, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 创建用户
     * @param userCreateRequest
     * @param source
     * @return
     */
    @PostMapping("/user/create")
    CommonResult<Long> create(@RequestBody @Valid UserCreateRequest userCreateRequest, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 获取用户头像
     * @param userId
     * @param source
     * @return
     */
    @GetMapping("/user/getAvatar/{userId}")
    String getAvatar(@PathVariable("userId") Long userId, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);


    /**
     * 获取用户资料
     * @param userId
     * @return
     */
    @GetMapping("/user/profile/{userId}")
    CommonResult<UserInfoVO> profile(@PathVariable("userId") Long userId);

}
