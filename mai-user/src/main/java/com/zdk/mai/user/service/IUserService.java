package com.zdk.mai.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zdk.mai.common.model.user.UserModel;
import com.zdk.mai.common.model.user.request.*;
import com.zdk.mai.common.model.user.vo.UserInfoVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Description
 * @Author zdk
 * @Date 2022/11/27 16:38
 */
public interface IUserService extends IService<UserModel> {

    /**
     * 根据用户名查询用户信息
     * @param username
     * @return
     */
    UserModel selectUserByUsername(String username);

    /**
     * 禁止用户发帖
     * @param username
     * @param value
     * @return
     */
    Boolean disabledPost(String username, boolean value);

    /**
     * 禁止用户评论
     * @param username
     * @param value
     * @return
     */
    Boolean disabledComment(String username, boolean value);

    /**
     * 新增用户(用于注册、后台管理新增)
     * @param userCreateRequest
     * @return 主键id
     */
    Long create(UserCreateRequest userCreateRequest);

    /**
     * 更新用户基本信息
     * @param updateRequest
     */
    void update(UserUpdateRequest updateRequest);

    /**
     * 获取用户详细信息
     * @param userId
     * @return
     */
    UserInfoVO profile(Long userId);


    /**
     * 头像上传
     * @param multipartFile
     * @param base 文件base路径 例如头像的是 /design/user/avatar/
     * @return
     */
    Boolean uploadAvatar(MultipartFile multipartFile, String base);

    /**
     * 获取用户头像
     * @param userId
     * @return
     */
    String getAvatar(Long userId);

    /**
     * 绑定登录用户邮箱
     * @param bindEmailRequest
     */
    void bindEmail(BindEmailRequest bindEmailRequest);


    /**
     * 解绑登录用户邮箱
     */
    void unbindEmail();

    /**
     * 判断邮箱是否已绑定
     * @param email
     * @return
     */
    Boolean emailExist(String email);


    /**
     * 重置密码
     * @param request
     */
    void resetPassword(ResetPasswordRequest request);

    /**
     * 邮箱重置密码
     * @param request
     */
    void emailResetPassword(EmailResetPasswordRequest request);


    /**
     * 用户列表 用于管理端
     * @param searchRequest
     * @return
     */
    PageInfo<UserModel> list(UserSearchRequest searchRequest);
}
