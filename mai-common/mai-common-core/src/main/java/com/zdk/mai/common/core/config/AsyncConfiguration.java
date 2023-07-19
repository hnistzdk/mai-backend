package com.zdk.mai.common.core.config;

import com.alibaba.ttl.threadpool.TtlExecutors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Description 异步线程池配置
 * @Author zdk
 * @Date 2023/3/17 18:11
 */
@Slf4j
@Configuration
@EnableAsync
public class AsyncConfiguration {

    /**
     * 邮箱线程池
     * @return
     */
    @Bean("mailTaskExecutor")
    public Executor mailTaskExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //核心线程数4：线程池创建时候初始化的线程数
        executor.setCorePoolSize(4);
        //最大线程数4：线程池最大的线程数，只有在缓冲队列满了之后才会申请超过核心线程数的线程
        executor.setMaxPoolSize(4);
        //缓冲队列500：用来缓冲执行任务的队列
        executor.setQueueCapacity(500);
        //允许线程的空闲时间60秒：当超过了核心线程出之外的线程在空闲时间到达之后会被销毁
        executor.setKeepAliveSeconds(60);
        //线程池名的前缀：设置好了之后可以方便我们定位处理任务所在的线程池
        executor.setThreadNamePrefix("MailAsync-");
        //拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.initialize();
        //使用阿里开源的可以非父子线程传递数据的线程池 防止参数丢失
        return TtlExecutors.getTtlExecutor(executor);
    }
}
