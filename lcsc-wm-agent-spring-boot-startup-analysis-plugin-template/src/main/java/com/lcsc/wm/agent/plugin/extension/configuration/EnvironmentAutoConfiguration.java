package com.lcsc.wm.agent.plugin.extension.configuration;

import com.lcsc.wm.agent.plugin.extension.environment.EnvironmentService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 通过注解实现对字节码的插桩(方法调用耗时统计插桩)
 */
@Configuration(proxyBeanMethods = false)
public class EnvironmentAutoConfiguration {

    @Bean
    public EnvironmentService environmentService() {
        return new EnvironmentService();
    }

}
