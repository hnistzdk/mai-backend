package com.zdk.mai.common.model.user.request;

import com.zdk.mai.common.core.request.BaseCreateRequest;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/2 17:27
 */
@Data
public class UserCreateRequest extends BaseCreateRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    /** 真实姓名 */
    private String realName;

    /** 自我介绍 */
    private String selfIntroduction;

    /** 用户昵称 */
    private String nickname;

    /** 用户邮箱 */
    private String email;

    /** 手机号码 */
    private String phone;

    /** 用户性别 */
    private String sex;

    /** 用户头像 */
    private String avatar;

    /** 毕业年份 */
    private String graduationYear;

    /** 学历 */
    private String educationalBackground;

    /** 毕业院校 */
    private String graduatedFrom;

    /** 专业 */
    private String specializedSubject;

}
