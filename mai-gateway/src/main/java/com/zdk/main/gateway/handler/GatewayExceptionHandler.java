package com.zdk.main.gateway.handler;

import com.zdk.mai.common.core.exception.BusinessException;
import com.zdk.mai.common.core.utils.ServletUtils;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Description 网关统一异常处理
 * @Author zdk
 * @Date 2023/3/4 12:39
 */
@Slf4j
@Order(-1)
@Configuration
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }

        log.error("[网关异常处理]请求路径:{},异常信息:{}", exchange.getRequest().getPath(), ex.getMessage());

        String msg;

        if (ex instanceof NotFoundException) {
            msg = "服务未找到";
            return ServletUtils.webFluxResponseWriter(response, msg);
        } else if (ex instanceof ResponseStatusException) {
            ResponseStatusException responseStatusException = (ResponseStatusException) ex;
            msg = responseStatusException.getMessage();
            return ServletUtils.webFluxResponseWriter(response, msg);
        } else if (ex instanceof ExpiredJwtException) {
            msg = "token已过期";
            return ServletUtils.webFluxResponseWriter(response, msg, 401);
        } else if (ex instanceof BusinessException) {
            BusinessException businessException = (BusinessException) ex;
            return ServletUtils.webFluxResponseWriter(response, businessException.getMessage(), businessException.getErrorCode());
        } else {
            msg = "服务器内部错误";
        }
        return ServletUtils.webFluxResponseWriter(response, msg);
    }
}
