package com.zdk.mai.common.security.handler;

import com.zdk.mai.common.core.exception.BusinessException;
import com.zdk.mai.common.core.exception.InnerAuthException;
import com.zdk.mai.common.core.exception.RateLimitException;
import com.zdk.mai.common.core.response.CommonResult;
import com.zdk.mai.common.security.enums.ErrorEnum;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Description 全局异常处理器
 * @Author zdk
 * @Date 2022/12/4 19:58
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常
     * @param e
     * @return
     */
    @ExceptionHandler({BusinessException.class})
    public CommonResult businessExceptionHandler(BusinessException e) {
        CommonResult<String> response = CommonResult.fail();
        log.error(e.getMessage(), e);
        response.setMsg(e.getMessage());
        if (e.getErrorCode() != null) {
            response.setCode(e.getErrorCode());
        }

        return response;
    }

    /**
     * 处理内部接口限流异常
     * @param e
     * @return
     */
    @ExceptionHandler({RateLimitException.class})
    public CommonResult rateLimitExceptionHandler(RateLimitException e) {
        CommonResult<String> response = CommonResult.fail();
        response.setMsg(e.getMessage());
        if (e.getCode() != null) {
            response.setCode(e.getCode());
        }

        return response;
    }


    /**
     * 处理 form data方式调用接口校验失败抛出的异常
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({BindException.class})
    public CommonResult validationExceptionHandler(Exception e) {
        log.error(e.getMessage(), e);
        return CommonResult.fail(ErrorEnum.BAD_REQUEST.getCode(), e.getMessage());
    }

    /**
     * 处理json请求体调用参数异常
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public CommonResult methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        StringBuilder errorMsg = new StringBuilder();
        BindingResult bindingResult = e.getBindingResult();
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        if (!allErrors.isEmpty()) {
            for (ObjectError objectError : allErrors) {
                if (objectError instanceof FieldError) {
                    FieldError fieldError = (FieldError) objectError;
                    errorMsg.append(fieldError.getField()).append(":").append(fieldError.getDefaultMessage()).append(";");
                } else {
                    errorMsg.append(objectError.getDefaultMessage()).append(";");
                }
            }
            return CommonResult.fail(errorMsg.toString());
        } else {
            return CommonResult.fail(e.getMessage());
        }
    }

    /**
     * 处理单个参数校验失败抛出的异常
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public CommonResult constraintViolationExceptionHandler(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        List<String> collect = constraintViolations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
        return CommonResult.fail(ErrorEnum.BAD_REQUEST.getCode(),collect.toString());
    }

    /**
     * 没有找到对应的url
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NoHandlerFoundException.class})
    public CommonResult noHandlerFoundExceptionHandler(NoHandlerFoundException e) {
        log.error(e.getMessage(), e);
        return CommonResult.fail(ErrorEnum.REQUEST_NOT_FOUND.getCode(), ErrorEnum.REQUEST_NOT_FOUND.getMsg());
    }

    /**
     * 数据库记录冲突
     * @param e
     * @return
     */
    @ExceptionHandler({DuplicateKeyException.class})
    public CommonResult duplicateKeyExceptionHandler(DuplicateKeyException e) {
        log.error(e.getMessage(), e);
        return CommonResult.fail("数据库中已存在该记录");
    }

    /**
     * 参数解析异常
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MissingServletRequestParameterException.class})
    public CommonResult missingServletRequestParameterExceptionHandler(Exception e) {
        log.error("参数解析失败", e);
        return CommonResult.fail(ErrorEnum.BAD_REQUEST.getCode(), ErrorEnum.BAD_REQUEST.getMsg());
    }

    /**
     * 请求方法错误
     * @param e
     * @param request
     * @return
     */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public CommonResult httpRequestMethodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',不支持'{}'请求", requestURI, e.getMethod());
        return CommonResult.fail(ErrorEnum.METHOD_NOT_ALLOWED.getCode(), ErrorEnum.METHOD_NOT_ALLOWED.getMsg());
    }

    /**
     * 普通异常
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({Exception.class})
    public CommonResult simpleExceptionHandler(Exception e) {
        log.error("系统异常", e);
        return CommonResult.fail(ErrorEnum.SYSTEM_ERROR.getCode(), ErrorEnum.SYSTEM_ERROR.getMsg());
    }

    /**
     * token错误异常
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({SignatureException.class})
    public CommonResult signatureExceptionHandler(SignatureException e) {
        log.error("token验证失败", e);
        return CommonResult.fail(ErrorEnum.NO_AUTHORIZATION.getCode(), "token验证失败");
    }

    /**
     * token过期异常
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({ExpiredJwtException.class})
    public CommonResult expiredJwtExceptionHandler (ExpiredJwtException e) {
        log.error("token已过期", e);
        return CommonResult.fail(ErrorEnum.NO_AUTHORIZATION.getCode(), "token已过期");
    }

    /**
     * 认证异常
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({InnerAuthException.class})
    public CommonResult innerAuthExceptionHandler (InnerAuthException e) {
        log.error(e.getMessage());
        return CommonResult.fail(ErrorEnum.NO_AUTHORIZATION.getCode(), ErrorEnum.NO_AUTHORIZATION.getMsg());
    }
}
