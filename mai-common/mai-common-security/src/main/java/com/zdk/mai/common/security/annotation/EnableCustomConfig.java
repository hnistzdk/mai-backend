package com.zdk.mai.common.security.annotation;

import com.zdk.mai.common.security.feign.FeignAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

import java.lang.annotation.*;

/**
 * @Description 自定义配置导入注解
 * @Author zdk
 * @Date 2023/3/7 18:15
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
// 表示通过aop框架暴露该代理对象,AopContext能够访问
@EnableAspectJAutoProxy(exposeProxy = true)
// 指定要扫描的Mapper类的包的路径
@MapperScan("com.zdk.**.mapper")
// 开启线程异步执行
@EnableAsync
// 自动加载类
@Import({FeignAutoConfiguration.class})
public @interface EnableCustomConfig {
}
