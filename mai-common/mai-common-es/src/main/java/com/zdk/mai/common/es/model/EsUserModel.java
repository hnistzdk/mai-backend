package com.zdk.mai.common.es.model;

import cn.easyes.annotation.IndexField;
import cn.easyes.annotation.IndexId;
import cn.easyes.annotation.IndexName;
import cn.easyes.annotation.rely.Analyzer;
import cn.easyes.annotation.rely.FieldType;
import cn.easyes.annotation.rely.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zdk.mai.common.es.EsBaseModel;
import lombok.Data;

import java.util.Date;

/**
 * @Description
 * @Author zdk
 * @Date 2023/4/7 19:40
 */
@Data
@IndexName("user")
public class EsUserModel extends EsBaseModel {

    /** 用户ID */
    @IndexId(type = IdType.CUSTOMIZE)
    private Long userId;

    /** 角色Id */
    private Long roleId;

    /** 用户账号 */
    @IndexField(fieldType = FieldType.TEXT,analyzer = Analyzer.IK_MAX_WORD,searchAnalyzer = Analyzer.IK_MAX_WORD)
    private String username;

    /** 真实姓名 */
    private String realName;

    /** 自我介绍 */
    @IndexField(fieldType = FieldType.TEXT,analyzer = Analyzer.IK_MAX_WORD,searchAnalyzer = Analyzer.IK_MAX_WORD)
    private String selfIntroduction;

    /** 用户昵称 */
    @IndexField(fieldType = FieldType.TEXT,analyzer = Analyzer.IK_MAX_WORD,searchAnalyzer = Analyzer.IK_MAX_WORD)
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
    @IndexField(fieldType = FieldType.TEXT,analyzer = Analyzer.IK_MAX_WORD,searchAnalyzer = Analyzer.IK_MAX_WORD)
    private String position;

    /** 公司 */
    @IndexField(fieldType = FieldType.TEXT,analyzer = Analyzer.IK_MAX_WORD,searchAnalyzer = Analyzer.IK_MAX_WORD)
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

    private Boolean delFlag;

}
