package com.lcsc.profiling.web.configuration;

import com.lcsc.profiling.web.test.copy.BeanCopyTestService;
import com.lcsc.profiling.web.test.env.EnvironmentTestService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class TestAgentAutoConfiguration {

    @Bean
    EnvironmentTestService environmentTestService(){
        return new EnvironmentTestService();
    }

    @Bean
    BeanCopyTestService beanCopyTestService(){
        return new BeanCopyTestService();
    }

}
