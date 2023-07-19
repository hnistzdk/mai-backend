package com.zdk.mai.common.model.user.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/18 19:12
 */
@Data
public class BindEmailRequest {

    @NotBlank(message = "邮箱不能为空")
    private String email;

    @NotBlank(message = "验证码不能为空")
    private String code;


}
