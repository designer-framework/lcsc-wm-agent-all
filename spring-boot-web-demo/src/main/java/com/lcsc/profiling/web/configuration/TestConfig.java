package com.lcsc.profiling.web.configuration;

import com.lcsc.profiling.web.annotation.Test;
import lombok.SneakyThrows;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-26 00:29
 */
@Test
@Configuration
public class TestConfig implements SmartInitializingSingleton {

    @Bean
    @SneakyThrows
    String stringBean() {
        Thread.sleep(66);
        return "";
    }

    @SneakyThrows
    @Override
    public void afterSingletonsInstantiated() {
        Thread.sleep(66);
    }

}
