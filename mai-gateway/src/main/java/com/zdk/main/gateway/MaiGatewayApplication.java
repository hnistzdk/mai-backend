package com.zdk.main.gateway;

import com.alibaba.csp.sentinel.transport.config.TransportConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Description
 * @Author zdk
 * @Date 2022/11/30 15:42
 */
@EnableDiscoveryClient
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class MaiGatewayApplication{
    public static void main(String[] args){
        //设置sentinel网关应用标识
//        System.setProperty("csp.sentinel.app.type", "1");
//        System.setProperty(TransportConfig.HEARTBEAT_CLIENT_IP, "localhost");
        SpringApplication.run(MaiGatewayApplication.class,args);
    }
}

