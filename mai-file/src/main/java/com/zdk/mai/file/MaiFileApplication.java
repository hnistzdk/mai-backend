package com.zdk.mai.file;

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
 * @Date 2023/3/5 19:44
 */
@EnableCustomSwagger2
@EnableCustomConfig
@EnableFeignClients(basePackages = {})
@EnableDiscoveryClient
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class MaiFileApplication{
    public static void main(String[] args){
        SpringApplication.run(MaiFileApplication.class,args);
    }
}
