package com.zdk.mai.message;

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
 * @Date 2023/2/23 22:19
 */
@EnableCustomSwagger2
@EnableCustomConfig
@EnableFeignClients(basePackages = {"com.zdk.mai.api.post","com.zdk.mai.api.comment"
        ,"com.zdk.mai.api.user"})
@EnableDiscoveryClient
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class MaiMessageApplication{
    public static void main(String[] args){
        SpringApplication.run(MaiMessageApplication.class,args);
    }
}
