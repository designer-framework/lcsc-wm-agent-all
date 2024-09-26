package com.lcsc.profiling.web.configuration;

import com.lcsc.profiling.web.annotation.Test;
import com.lcsc.profiling.web.test.lifecycle.SmartInitializingSingletonService;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-26 00:29
 */
@Test
@Configuration
public class SmartInitializingSingletonAutoConfiguration {

    @Bean
    @SneakyThrows
    String sleep166msBean() {
        Thread.sleep(166);
        return "166ms";
    }

    @Bean
    public SmartInitializingSingletonService smartInitializingSingletonService() {
        return new SmartInitializingSingletonService();
    }

}
