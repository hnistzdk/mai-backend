package com.zdk.mai.user;

import cn.easyes.starter.register.EsMapperScan;
import com.zdk.mai.common.security.annotation.EnableCustomConfig;
import com.zdk.mai.common.swagger.annotation.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Description
 * @Author zdk
 * @Date 2023/2/24 10:25
 */

@EnableCustomSwagger2
@EsMapperScan("com.zdk.mai.common.es.mapper")
@EnableScheduling
@EnableCustomConfig
@EnableFeignClients(basePackages = {"com.zdk.mai.api.post","com.zdk.mai.api.comment","com.zdk.mai.api.file"})
@EnableDiscoveryClient
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class MaiUserApplication{
    public static void main(String[] args){
        SpringApplication.run(MaiUserApplication.class,args);
    }
}
