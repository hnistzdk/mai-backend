package com.zdk.mai.common.model.user.vo;

import com.zdk.mai.common.model.user.UserModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Set;

/**
 * @Description
 * @Author zdk
 * @Date 2022/11/27 16:52
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class LoginUserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * token
     */
    private String token;

    /**
     * 用户id
     */
    private Long userId;

    /** 角色Id */
    private Long roleId;

    /** 名称Id */
    private String roleName;

    /**
     * 用户名
     */
    private String username;

    /**
     * 登录时间
     */
    private Long loginTime;

    /**
     * 过期时间
     */
    private Long expireTime;

    /**
     * 登录IP地址
     */
    private String ipaddr;

    /**
     * 权限列表
     */
    private Set<String> permissions;

    /**
     * 用户信息
     */
    private UserModel userModel;

}
