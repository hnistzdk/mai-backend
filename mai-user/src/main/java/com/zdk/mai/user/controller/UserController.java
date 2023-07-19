package com.zdk.mai.user.controller;

import cn.hutool.core.util.RandomUtil;
import com.zdk.mai.common.core.constant.CacheConstants;
import com.zdk.mai.common.core.constant.CommonConstants;
import com.zdk.mai.common.core.exception.BusinessException;
import com.zdk.mai.common.core.response.CommonResult;
import com.zdk.mai.common.log.annotation.AccessLog;
import com.zdk.mai.common.log.enums.BusinessType;
import com.zdk.mai.common.model.role.RoleModel;
import com.zdk.mai.common.model.user.UserModel;
import com.zdk.mai.common.model.user.request.*;
import com.zdk.mai.common.model.user.vo.LoginUserVO;
import com.zdk.mai.common.model.user.vo.UserInfoVO;
import com.zdk.mai.common.redis.service.RedisService;
import com.zdk.mai.common.security.annotation.*;
import com.zdk.mai.common.security.enums.ErrorEnum;
import com.zdk.mai.user.service.IRoleService;
import com.zdk.mai.user.service.IUserService;
import com.zdk.mai.user.service.impl.MailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * @Description
 * @Author zdk
 * @Date 2022/11/27 16:30
 */
@Api(tags = "用户相关接口")
@Validated
@RestController
public class UserController {

    private static final String DISABLED_STATUS = "1";

    @Autowired
    private IUserService userService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private MailService mailService;

    @Autowired
    private IRoleService roleService;


    @ApiOperation(value = "获取用户信息(远程调用)", notes = "获取用户信息(远程调用)")
    @InnerAuth
    @AccessLog(title = "获取用户信息",businessType = BusinessType.SELECT)
    @GetMapping("/user/info/{username}")
    public CommonResult<LoginUserVO> info(@PathVariable("username") String username){
        UserModel userModel = userService.selectUserByUsername(username);
        String status = userModel.getStatus();
        if (status.equals(DISABLED_STATUS)){
            return CommonResult.fail("用户已被禁用,请联系管理员");
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(userModel, loginUserVO);
        loginUserVO.setUserModel(userModel);

        //权限相关
        RoleModel role = roleService.getById(userModel.getRoleId());
        loginUserVO.setRoleName(role.getRoleName());
        String roleKeyStr = role.getRoleKey();
        if (StringUtils.isNotEmpty(roleKeyStr)){

            String[] roleKeys = roleKeyStr.split(",");
            Set<String> permissions = Arrays.stream(roleKeys).collect(Collectors.toSet());
            loginUserVO.setPermissions(permissions);
        }

        return CommonResult.success(loginUserVO);
    }

    @ApiOperation(value = "新增用户", notes = "新增用户")
    @InnerAuth
    @AccessLog(title = "新增用户",businessType = BusinessType.INSERT)
    @InitRequestArg(argType = ArgType.CREATE)
    @PostMapping("/user/create")
    public CommonResult<Long> create(@RequestBody @Valid UserCreateRequest userCreateRequest){
        Long userId = userService.create(userCreateRequest);
        return CommonResult.success(userId);
    }

    @ApiOperation(value = "更新用户基本信息", notes = "更新用户基本信息")
    @AccessLog(title = "更新用户基本信息",businessType = BusinessType.UPDATE)
    @PostMapping("/user/update")
    public CommonResult<Long> update(@RequestBody @Valid UserUpdateRequest updateRequest){
        userService.update(updateRequest);
        return CommonResult.success();
    }

    @ApiOperation(value = "验证用户名是否重复", notes = "验证用户名是否重复")
    @NotRequiresLogin
    @AccessLog(title = "验证用户名是否重复",businessType = BusinessType.SELECT)
    @GetMapping("/user/verify")
    public CommonResult verifyUsername(@RequestParam("username") @NotBlank String username){
        Boolean exist = redisService.hasKey(CacheConstants.USER_EXIST_KEY + username);
        if (exist){
            return CommonResult.fail("该用户名已存在");
        }
        return CommonResult.success();
    }

    @ApiOperation(value = "头像上传", notes = "头像上传")
    @RateLimiter(prefix = "user",key = "uploadAvatar",limitIp = false, expireTime = 60*60*24,maxCount = 1,rateMessage = "每天只能上传1次头像")
    @PostMapping(value = "/user/avatar")
    public CommonResult uploadAvatar(@RequestParam(name = "avatar") MultipartFile multipartFile,String base) {

        Boolean res = userService.uploadAvatar(multipartFile, base);

        return CommonResult.getResult(res);
    }


    @ApiOperation(value = "Id查看用户信息", notes = "查看用户信息")
    @AccessLog(title = "查看用户信息",businessType = BusinessType.SELECT)
    @GetMapping("/user/profile/{userId}")
    public CommonResult<UserInfoVO> profile(@PathVariable("userId") Long userId){
        return CommonResult.success(userService.profile(userId));
    }

    @ApiOperation(value = "获取用户头像", notes = "获取用户头像")
    @InnerAuth
    @AccessLog(title = "获取用户头像",businessType = BusinessType.SELECT)
    @GetMapping("/user/getAvatar/{userId}")
    public String getAvatar(@PathVariable("userId") Long userId){
        return userService.getAvatar(userId);
    }


    @ApiOperation(value = "校验是否过期", notes = "校验是否过期")
    @AccessLog(title = "校验是否过期",businessType = BusinessType.SELECT)
    @GetMapping("/user/validate")
    public CommonResult validate(){
        return CommonResult.success();
    }



    @ApiOperation(value = "发送邮箱验证码", notes = "发送邮箱验证码")
    @RateLimiter(prefix = "user",key = "sendEmailVerifyCode", expireTime = 60,maxCount = 1,rateMessage = "1分钟只能进行1次操作")
    @NotRequiresLogin
    @GetMapping("/user/sendEmailVerifyCode")
    public CommonResult sendEmailVerifyCode(@RequestParam("email") @NotBlank String email,
                                            @RequestParam("type") @NotBlank String type) {

        //如果是绑定邮箱发验证码 判一下
        if (type.equals(CommonConstants.EMAIL_CODE_TYPE_BIND) && userService.emailExist(email)){
            throw new BusinessException(ErrorEnum.EMAIL_DUPLICATION_ERROR.getCode(), ErrorEnum.EMAIL_DUPLICATION_ERROR.getMsg());
        }

        String randomCode = RandomUtil.randomNumbers(6);

        mailService.sendEmail(email,"验证码,请在一分钟之内完成验证", randomCode);
        //验证码存入redis 过期时间60秒
        redisService.setCacheObject(CacheConstants.EMAIL_CODE_KEY+ type + email, randomCode, 60L, TimeUnit.SECONDS);

        return CommonResult.success();
    }

    @ApiOperation(value = "解绑邮箱", notes = "解绑邮箱")
    @PostMapping("/user/unbindEmail")
    public CommonResult unbindEmail() {
        userService.unbindEmail();
        return CommonResult.success();
    }

    @ApiOperation(value = "绑定邮箱", notes = "绑定邮箱")
    @PostMapping("/user/bindEmail")
    public CommonResult bindEmail(@RequestBody @Valid BindEmailRequest bindEmailRequest) {
        userService.bindEmail(bindEmailRequest);
        return CommonResult.success();
    }



    @ApiOperation(value = "登录后重置密码", notes = "登录后重置密码")
    @PostMapping("/user/resetPassword")
    public CommonResult resetPassword(@RequestBody @Valid ResetPasswordRequest resetPasswordRequest) {

        userService.resetPassword(resetPasswordRequest);

        return CommonResult.success();
    }

    @ApiOperation(value = "判断邮箱是否已绑定", notes = "判断邮箱是否已绑定")
    @NotRequiresLogin
    @PostMapping("/user/emailExist/{email}")
    public CommonResult emailExist(@PathVariable("email") String email) {

        Boolean res = userService.emailExist(email);

        return CommonResult.getResult(res);
    }

    @ApiOperation(value = "邮箱重置密码", notes = "邮箱重置密码")
    @NotRequiresLogin
    @PostMapping("/user/emailResetPassword")
    public CommonResult emailResetPassword(@RequestBody @Valid EmailResetPasswordRequest emailResetPasswordRequest) {

        userService.emailResetPassword(emailResetPasswordRequest);

        return CommonResult.success();
    }






    @ApiOperation(value = "禁止或允许用户发帖", notes = "禁止或允许用户发帖")
    @RequiresRoles(value = {"admin"})
    @PostMapping("/user/disabled/post/{username}/{value}")
    public CommonResult disabledPost(@PathVariable("username") String username,
                                     @PathVariable("value") boolean value){
        return CommonResult.getResult(userService.disabledPost(username,value)).setData(!value);
    }


    @ApiOperation(value = "禁止或允许用户评论", notes = "禁止或允许用户评论")
    @RequiresRoles(value = {"admin"})
    @PostMapping("/user/disabled/comment/{username}/{value}")
    public CommonResult disabledComment(@PathVariable("username") String username,
                                        @PathVariable("value") boolean value){
        return CommonResult.getResult(userService.disabledComment(username,value)).setData(!value);
    }


}
