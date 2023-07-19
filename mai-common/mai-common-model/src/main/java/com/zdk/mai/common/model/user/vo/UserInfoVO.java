package com.zdk.mai.common.model.user.vo;

import com.zdk.mai.common.core.response.BaseVO;
import lombok.Data;

import java.util.Date;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/3 16:01
 */
@Data
public class UserInfoVO extends BaseVO {
    private Long userId;

    /** 角色Id */
    private Long roleId;

    private String roleName;

    private String username;

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

    /** 职位 */
    private String position;

    /** 公司 */
    private String company;

    /** 毕业年份 */
    private Integer graduationYear;

    /** 学历 */
    private String educationalBackground;

    /** 毕业院校 */
    private String graduatedFrom;

    /** 专业 */
    private String specializedSubject;

    /** 最后登录IP */
    private String loginIp;

    /** 最后登录时间 */
    private Date loginDate;

    /** 帐号状态（0正常 1停用 2删除） */
    private String status;

    /** 禁止发帖标志（0代表正常 1代表禁止） */
    private Boolean postDisabled;
    /** 禁止评论标志（0代表正常 1代表禁止） */
    private Boolean commentDisabled;
    /** 经验*/
    private Long points;
    /** 等级*/
    private String level;

    /** 贴子获赞总数*/
    private Long likeCount;

    /** 贴子被阅读总数*/
    private Long readCount;

    /** 是否关注了此用户*/
    private Boolean isFollow;
}
