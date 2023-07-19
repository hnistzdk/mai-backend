package com.zdk.mai.auth.service;

import com.zdk.mai.api.user.RemoteUserService;
import com.zdk.mai.common.model.user.request.LoginRequest;
import com.zdk.mai.common.core.constant.CacheConstants;
import com.zdk.mai.common.core.constant.SecurityConstants;
import com.zdk.mai.common.core.enums.UserStatusEnum;
import com.zdk.mai.common.core.exception.BusinessException;
import com.zdk.mai.common.core.response.CommonResult;
import com.zdk.mai.common.model.user.UserModel;
import com.zdk.mai.common.model.user.request.UserCreateRequest;
import com.zdk.mai.common.model.user.vo.LoginUserVO;
import com.zdk.mai.common.security.service.TokenService;
import com.zdk.mai.common.security.utils.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description 认证相关逻辑
 * @Author zdk
 * @Date 2022/11/27 16:04
 */
@Service
public class AuthService {


    @Autowired
    private RemoteUserService remoteUserService;


    @Autowired
    private TokenService tokenService;


    /**
     * 登录逻辑实现
     * @param loginRequest
     * @return
     */
    public LoginUserVO login(LoginRequest loginRequest){
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        // 获取用户信息
        CommonResult<LoginUserVO> result = remoteUserService.getUserInfo(username, SecurityConstants.INNER);
        if (Boolean.TRUE.equals(CommonResult.isError(result))){
            throw new BusinessException(result.getMsg());
        }
        if (result == null || result.getData() == null){
            throw new BusinessException("登录用户：" + username + " 不存在或已被删除");
        }
        LoginUserVO userInfo = result.getData();
        UserModel userModel = userInfo.getUserModel();
        //被禁用
        if (UserStatusEnum.DISABLE.getCode().equals(userModel.getStatus())){
            throw new BusinessException("对不起，您的账号：" + username + " 已停用");
        }
        //无误后 验证
        validate(userModel, password);
        BeanUtils.copyProperties(userModel, userInfo);
        //创建token 携带到返回vo中
        return userInfo;
    }

    /**
     * 注册逻辑实现
     * @param registerRequest
     * @return
     */
    public LoginUserVO register(LoginRequest registerRequest){
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        BeanUtils.copyProperties(registerRequest, userCreateRequest);
        //创建用户
        CommonResult<Long> commonResult = remoteUserService.create(userCreateRequest, SecurityConstants.INNER);
        LoginUserVO loginUserVO = new LoginUserVO();
        loginUserVO.setUserId(commonResult.getData());
        loginUserVO.setUsername(registerRequest.getUsername());
        return loginUserVO;
    }

    /**
     * 验证密码
     * @param userModel
     * @param password
     */
    private void validate(UserModel userModel,String password){
        String encodePassword = userModel.getPassword();
        String username = userModel.getUsername();
        //先检查密码错误次数是否超过限制
        if (tokenService.verifyPwdErrCount(username)){
            String errMsg = String.format("密码输入错误%s次，帐户锁定%s分钟", CacheConstants.PASSWORD_MAX_RETRY_COUNT, CacheConstants.PASSWORD_LOCK_TIME);
            throw new BusinessException(errMsg);
        }
        //校验密码
        if (!SecurityUtils.matchPassword(password, encodePassword)){
            tokenService.addPwdErrCount(username);
            String errMsg = String.format("密码输入错误%s次，帐户锁定%s分钟", CacheConstants.PASSWORD_MAX_RETRY_COUNT, CacheConstants.PASSWORD_LOCK_TIME);
            throw new BusinessException("密码错误,请重新输入");
        }

    }

}
