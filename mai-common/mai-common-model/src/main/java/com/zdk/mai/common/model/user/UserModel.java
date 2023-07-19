package com.zdk.mai.common.model.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zdk.mai.common.model.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @Description 用户表
 * @Author zdk
 * @Date 2022/11/27 15:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("mai_user")
public class UserModel extends BaseModel {

    private static final long serialVersionUID = 1L;

    /** 用户ID */
    @TableId(value = "user_id",type = IdType.ASSIGN_ID)
    private Long userId;

    /** 角色Id */
    private Long roleId;

    /** 用户账号 */
    private String username;

    /** 密码 */
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
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date loginDate;

    /** 帐号状态（0正常 1停用 2删除） */
    private String status;

    /** 禁止发帖标志（0代表正常 1代表禁止） */
    private Boolean postDisabled;
    /** 禁止评论标志（0代表正常 1代表禁止） */
    private Boolean commentDisabled;
}
