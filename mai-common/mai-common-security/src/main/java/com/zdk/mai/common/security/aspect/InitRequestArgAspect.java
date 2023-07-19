package com.zdk.mai.common.security.aspect;

import com.zdk.mai.common.core.constant.CommonConstants;
import com.zdk.mai.common.core.request.BaseCreateRequest;
import com.zdk.mai.common.core.request.BaseDeleteRequest;
import com.zdk.mai.common.core.request.BaseRequest;
import com.zdk.mai.common.core.request.BaseUpdateRequest;
import com.zdk.mai.common.security.annotation.ArgType;
import com.zdk.mai.common.security.annotation.InitRequestArg;
import com.zdk.mai.common.security.utils.SecurityUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Date;

/**
 * @Description 初始化controller请求入参
 * @Author zdk
 * @Date 2023/2/28 20:17
 */

@Aspect
@Component
public class InitRequestArgAspect {

    @Pointcut("@annotation(com.zdk.mai.common.security.annotation.InitRequestArg)")
    public void pointcut() {
    }


    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        //首先拿注解 如果存在才做增强
        InitRequestArg annotation = method.getAnnotation(InitRequestArg.class);
        if (annotation != null){
            ArgType argType = annotation.argType();
            Object[] args = joinPoint.getArgs();
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (arg instanceof BaseRequest){
                    // 新增操作
                    if (argType == ArgType.CREATE){
                        BaseCreateRequest baseRequest = (BaseCreateRequest) arg;
                        baseRequest.setCreateBy(SecurityUtils.getUserId());
                        baseRequest.setCreateTime(new Date());
                        baseRequest.setUpdateBy(SecurityUtils.getUserId());
                        baseRequest.setUpdateTime(new Date());
                        baseRequest.setDelFlag(Boolean.FALSE);
                        baseRequest.setRowVersion(CommonConstants.ROW_VERSION);
                        args[i] = baseRequest;
                    }
                    // 更新操作
                    else if (argType == ArgType.UPDATE){
                        BaseUpdateRequest baseRequest = (BaseUpdateRequest) arg;
                        baseRequest.setUpdateBy(SecurityUtils.getUserId());
                        baseRequest.setUpdateTime(new Date());
                        args[i] = baseRequest;
                    }
                    // 删除操作
                    else if (argType == ArgType.DELETE){
                        BaseDeleteRequest baseRequest = (BaseDeleteRequest) arg;
                        baseRequest.setUpdateBy(SecurityUtils.getUserId());
                        baseRequest.setUpdateTime(new Date());
                        baseRequest.setDelFlag(Boolean.TRUE);
                        args[i] = baseRequest;
                    }
                }
            }
            return joinPoint.proceed(args);
        }else {
            //执行原有逻辑
            return joinPoint.proceed();
        }
    }
}
