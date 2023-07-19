package com.zdk.mai.common.security.utils;

import com.zdk.mai.common.core.constant.SecurityConstants;
import com.zdk.mai.common.core.context.SecurityContextHolder;
import com.zdk.mai.common.core.utils.ServletUtils;
import com.zdk.mai.common.model.user.vo.LoginUserVO;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description
 * @Author zdk
 * @Date 2023/2/26 20:20
 */
public class SecurityUtils {
    /**
     * 获取用户ID
     */
    public static Long getUserId() {
        return SecurityContextHolder.getUserId();
    }

    /**
     * 获取用户名称
     */
    public static String getUsername() {
        return SecurityContextHolder.getUserName();
    }

    /**
     * 获取用户key
     */
    public static String getUserKey() {
        return SecurityContextHolder.getUserKey();
    }

    /**
     * 获取登录用户信息
     */
    public static LoginUserVO getLoginUser() {
        return SecurityContextHolder.get(SecurityConstants.LOGIN_USER, LoginUserVO.class);
    }

    /**
     * 获取请求token
     */
    public static String getToken() {
        return getToken(ServletUtils.getRequest());
    }


    /**
     * 根据request获取请求token
     */
    public static String getToken(HttpServletRequest request) {
        // 从header获取token标识
        return request.getHeader(SecurityConstants.AUTHORIZATION_HEADER);
    }


    /**
     * 生成BCryptPasswordEncoder密码
     *
     * @param password 密码
     * @return 加密字符串
     */
    public static String encryptPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    /**
     * 判断密码是否相同
     *
     * @param rawPassword     真实密码
     * @param encodedPassword 加密后字符
     * @return 结果
     */
    public static boolean matchPassword(String rawPassword, String encodedPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

}
