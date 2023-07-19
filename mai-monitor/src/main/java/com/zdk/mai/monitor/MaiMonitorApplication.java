package com.zdk.mai.monitor;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Description
 * @Author zdk
 * @Date 2023/4/12 16:56
 */
@EnableAdminServer
@SpringBootApplication
public class MaiMonitorApplication {
    public static void main(String[] args) {
        SpringApplication.run(MaiMonitorApplication.class, args);
    }
}
