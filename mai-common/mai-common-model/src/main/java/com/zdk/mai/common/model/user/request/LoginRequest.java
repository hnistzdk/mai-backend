package com.zdk.mai.common.model.user.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Description
 * @Author zdk
 * @Date 2022/12/4 19:54
 */
@Data
public class LoginRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
