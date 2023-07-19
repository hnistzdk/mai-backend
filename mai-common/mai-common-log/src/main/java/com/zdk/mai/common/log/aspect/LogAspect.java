package com.zdk.mai.common.log.aspect;

import com.zdk.mai.common.log.annotation.AccessLog;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description
 * @Author zdk
 * @Date 2023/2/26 18:33
 */
@Aspect
@Component
public class LogAspect {

    protected final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    ThreadLocal<Long> startTime = new ThreadLocal<>();

    /**
     * 访问日志配置织入点(controller中并且以AccessLog注解为标志)
     */
    @Pointcut("@annotation(com.zdk.mai.common.log.annotation.AccessLog)")
    public void accessLogPointCut() {
    }

    /**
     * 访问前记录
     * @param joinPoint
     */
    @Before("accessLogPointCut()")
    public void doBefore(JoinPoint joinPoint) {
        startTime.set(System.currentTimeMillis());
        //接收到请求，记录请求内容
        HttpServletRequest request = getHttpServletRequest();
        // 记录下请求内容
        logger.info("URL : {}", request.getRequestURL());
        logger.info("HTTP_METHOD : {}", request.getMethod());
        logger.info("IP : {}", getRemoteHost(request));
        logger.info("CLASS_METHOD : {}", joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        logger.info("ARGS : {}", joinPoint.getArgs());
    }

    /**
     * 访问日志
     * 返回后通知（@AfterReturning）
     * returning  注解返回值
     *
     * @param joinPoint
     * @param returnValue 返回值
     * @throws Exception
     */
    @AfterReturning(pointcut = "@annotation(accessLog)", returning = "returnValue")
    public void accessAfterReturning(JoinPoint joinPoint,AccessLog accessLog, Object returnValue) {
        try {
            //方法注解实体
            if (accessLog == null) {
                return;
            }
            logger.info("SPEND TIME : {}ms",System.currentTimeMillis() - startTime.get());
            startTime.remove();
        } catch (Exception e) {
            logger.error("记录访问日志异常！");
        }

    }


    /**
     * 获取当前的request
     *
     * @return
     */
    public HttpServletRequest getHttpServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        assert servletRequestAttributes != null;
        return servletRequestAttributes.getRequest();
    }

    /**
     * 获取目标主机的ip
     *
     * @param request
     * @return
     */
    private String getRemoteHost(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }

}
