package com.zdk.mai.comment;

import cn.easyes.starter.register.EsMapperScan;
import com.zdk.mai.common.security.annotation.EnableCustomConfig;
import com.zdk.mai.common.swagger.annotation.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Description
 * @Author zdk
 * @Date 2023/2/23 22:18
 */
@EnableCustomSwagger2
@EsMapperScan("com.zdk.mai.common.es.mapper")
@EnableCustomConfig
@EnableFeignClients(basePackages = {"com.zdk.mai.api.user","com.zdk.mai.api.post"})
@EnableDiscoveryClient
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class MaiCommentApplication{
    public static void main(String[] args){
        SpringApplication.run(MaiCommentApplication.class,args);
    }
}
