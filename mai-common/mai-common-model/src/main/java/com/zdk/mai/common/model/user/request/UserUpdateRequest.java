package com.zdk.mai.common.model.user.request;

import com.zdk.mai.common.core.request.BaseUpdateRequest;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/2 17:22
 */
@Data
public class UserUpdateRequest extends BaseUpdateRequest {

    @NotNull
    private Long userId;

    /** 角色Id */
    private Long roleId;

    private String sex;

    @Length(max = 10)
    private String nickname;

    /** 职位 */
    @Length(max = 20)
    private String position;

    /** 公司 */
    @Length(max = 50)
    private String company;

    /** 自我介绍 */
    @Length(max = 150)
    private String selfIntroduction;

    /** 毕业年份 */
    private Integer graduationYear;

    /** 学历 */
    private String educationalBackground;

    /** 毕业院校 */
    private String graduatedFrom;

    /** 专业 */
    private String specializedSubject;

    /** 0正常 1停用*/
    private String status;

    private String password;

}
