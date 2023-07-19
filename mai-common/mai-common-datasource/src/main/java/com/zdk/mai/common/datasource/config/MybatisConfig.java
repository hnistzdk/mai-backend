package com.zdk.mai.common.datasource.config;

import com.zdk.mai.common.datasource.inteceptors.MybatisPlusSqlInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description
 * @Author zdk
 * @Date 2023/3/10 16:25
 */
@Configuration
public class MybatisConfig {

    @Bean
    public MybatisPlusSqlInterceptor mybatisPlusSqlInterceptor(){
        return new MybatisPlusSqlInterceptor();
    }

}
