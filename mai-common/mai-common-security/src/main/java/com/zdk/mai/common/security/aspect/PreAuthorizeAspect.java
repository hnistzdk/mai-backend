package com.zdk.mai.common.security.aspect;

import com.zdk.mai.common.security.annotation.NotRequiresLogin;
import com.zdk.mai.common.security.annotation.RequiresLogin;
import com.zdk.mai.common.security.annotation.RequiresPermissions;
import com.zdk.mai.common.security.annotation.RequiresRoles;
import com.zdk.mai.common.security.auth.AuthUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @Description
 * @Author zdk
 * @Date 2023/2/26 22:03
 * 权限校验
 */
@Aspect
@Component
public class PreAuthorizeAspect {

    /**
     * 定义AOP签名 (切入所有使用鉴权注解的方法)
     */
    public static final String POINTCUT_SIGN = " @annotation(com.zdk.mai.common.security.annotation.RequiresLogin) || "
            + "@annotation(com.zdk.mai.common.security.annotation.NotRequiresLogin) || "
            + "@annotation(com.zdk.mai.common.security.annotation.RequiresPermissions) || "
            + "@annotation(com.zdk.mai.common.security.annotation.RequiresRoles)";

    /**
     * 声明AOP签名
     */
    @Pointcut(POINTCUT_SIGN)
    public void pointcut() {
    }

    /**
     * 环绕切入
     *
     * @param joinPoint 切面对象
     * @return 底层方法执行后的返回值
     * @throws Throwable 底层方法抛出的异常
     */
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 注解鉴权
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        checkMethodAnnotation(signature.getMethod());
        // 执行原有逻辑
        return joinPoint.proceed();
    }

    /**
     * 对一个Method对象进行注解检查
     */
    public void checkMethodAnnotation(Method method) {
        // 校验 @NotRequiresLogin 注解
        NotRequiresLogin notRequiresLogin = method.getAnnotation(NotRequiresLogin.class);
        if (notRequiresLogin != null) {
            return;
        }

        // 校验 @RequiresLogin 注解
        RequiresLogin requiresLogin = method.getAnnotation(RequiresLogin.class);
        if (requiresLogin != null) {
//            AuthUtil.checkLogin();
        }

        // 校验 @RequiresRoles 注解
        RequiresRoles classRequireRoles = method.getClass().getAnnotation(RequiresRoles.class);
        RequiresRoles requiresRoles = method.getAnnotation(RequiresRoles.class);
        if (requiresRoles != null) {
//            AuthUtil.checkRole(requiresRoles);
        }

        if (classRequireRoles != null){
            AuthUtil.checkRole(classRequireRoles);
        }

        // 校验 @RequiresPermissions 注解
        RequiresPermissions requiresPermissions = method.getAnnotation(RequiresPermissions.class);
        if (requiresPermissions != null) {
//            AuthUtil.checkPermi(requiresPermissions);
        }
    }
}
