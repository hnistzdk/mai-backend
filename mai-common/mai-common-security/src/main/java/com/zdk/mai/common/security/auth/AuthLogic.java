package com.zdk.mai.common.security.auth;

import com.zdk.mai.common.core.constant.SecurityConstants;
import com.zdk.mai.common.core.exception.BusinessException;
import com.zdk.mai.common.core.utils.SpringContextUtils;
import com.zdk.mai.common.model.user.vo.LoginUserVO;
import com.zdk.mai.common.security.annotation.RequiresRoles;
import com.zdk.mai.common.security.enums.ErrorEnum;
import com.zdk.mai.common.security.service.TokenService;
import com.zdk.mai.common.security.utils.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.PatternMatchUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @Description 认证逻辑实现
 * @Author zdk
 * @Date 2023/3/3 13:41
 */
public class AuthLogic {

    public TokenService tokenService = SpringContextUtils.getBean(TokenService.class);

    public void logout(){
        String token = SecurityUtils.getToken();
        if (StringUtils.isEmpty(token)) {
            return;
        }
        tokenService.deleteLoginUser(token);
    }

    /**
     * 判断是否是管理员
     * @return
     */
    public boolean isAdmin(){
        LoginUserVO loginUserVO = tokenService.getLoginUser();
        if (loginUserVO == null){
            return false;
        }
        return StringUtils.isNotEmpty(loginUserVO.getRoleName()) && loginUserVO.getRoleName().equals(SecurityConstants.ADMIN_ROLE_NAME);
    }

    public boolean isAdmin(LoginUserVO loginUserVO){
        return StringUtils.isNotEmpty(loginUserVO.getRoleName()) && loginUserVO.getRoleName().equals(SecurityConstants.ADMIN_ROLE_NAME);
    }

    public void checkRole(RequiresRoles requiresRoles) {
        String[] roles = requiresRoles.value();
        String role = roles[0];
        String userRole = getRole();
        if (!userRole.equals(role)){
            throw new BusinessException(ErrorEnum.UNAUTHORIZED.getCode(), ErrorEnum.UNAUTHORIZED.getMsg());
        }
    }

    /**
     * 获取当前账号的角色列表
     *
     * @return 角色列表
     */
    public String getRole() {
        LoginUserVO loginUser = tokenService.getLoginUser();
        if (loginUser != null){
            return loginUser.getRoleName();
        }
        return null;
    }
}
