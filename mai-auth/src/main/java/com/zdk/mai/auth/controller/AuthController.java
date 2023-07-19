package com.zdk.mai.auth.controller;

import com.zdk.mai.common.model.user.request.LoginRequest;
import com.zdk.mai.auth.service.AuthService;
import com.zdk.mai.common.core.response.CommonResult;
import com.zdk.mai.common.model.user.vo.LoginUserVO;
import com.zdk.mai.common.security.auth.AuthUtil;
import com.zdk.mai.common.security.service.TokenService;
import com.zdk.mai.common.security.utils.SecurityUtils;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @Description
 * @Author zdk
 * @Date 2022/11/27 16:04
 */
@Validated
@RestController
public class AuthController {

    @Resource
    private AuthService authService;

    @Autowired
    private TokenService tokenService;

    @ApiOperation(value = "登录", notes = "登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "loginRequest", value = "loginRequest", required = true, dataType = "LoginRequest"),
    })
    @PostMapping("/auth/login")
    public CommonResult authLogin(@RequestBody @Valid LoginRequest loginRequest){
        LoginUserVO loginUserVO = authService.login(loginRequest);
        return CommonResult.success(tokenService.createToken(loginUserVO));
    }

    @ApiOperation(value = "注册", notes = "注册")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "registerRequest", value = "registerRequest", required = true, dataType = "LoginRequest"),
    })
    @PostMapping("/auth/register")
    public CommonResult authRegister(@RequestBody @Valid LoginRequest registerRequest){
        LoginUserVO loginUserVO = authService.register(registerRequest);
        return CommonResult.success(tokenService.createToken(loginUserVO));
    }

    @ApiOperation(value = "注销登录", notes = "注销登录")
    @PostMapping("/auth/logout")
    public CommonResult authRegister(){
        AuthUtil.logout();
        return CommonResult.success();
    }
}
