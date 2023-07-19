package com.zdk.mai.common.security.auth;

import com.zdk.mai.common.model.user.vo.LoginUserVO;
import com.zdk.mai.common.security.annotation.RequiresRoles;

/**
 * @Description 认证工具类
 * @Author zdk
 * @Date 2023/3/3 13:33
 */
public class AuthUtil {

    public static AuthLogic authLogic = new AuthLogic();

    public static void logout() {
        authLogic.logout();
    }

    public static boolean isAdmin() {
        return authLogic.isAdmin();
    }

    public static boolean isAdmin(LoginUserVO loginUserVO) {
        return authLogic.isAdmin(loginUserVO);
    }

    public static void checkRole(RequiresRoles requiresRoles) {
        authLogic.checkRole(requiresRoles);
    }


}
