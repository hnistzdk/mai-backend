package com.zdk.mai.common.model.user.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Description 登录后重置密码request
 * @Author zdk
 * @Date 2023/3/18 22:35
 */
@Data
public class ResetPasswordRequest {

    @NotBlank(message = "原密码不能为空")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}
