package com.zdk.mai.common.model.user.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Description 忘记密码邮箱重置request
 * @Author zdk
 * @Date 2023/3/19 15:51
 */
@Data
public class EmailResetPasswordRequest {

    @NotBlank(message = "邮箱不能为空")
    private String email;

    @NotBlank(message = "验证码不能为空")
    private String code;

    @NotBlank(message = "新密码不能为空")
    private String newPassword;

}
