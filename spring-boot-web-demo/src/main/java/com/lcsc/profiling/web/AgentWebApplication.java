package com.lcsc.profiling.web;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableApolloConfig
@EnableFeignClients
@EnableDiscoveryClient(autoRegister = false)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@SpringBootApplication(scanBasePackages = {"com.lcsc"})
public class AgentWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgentWebApplication.class, args);
    }

}
