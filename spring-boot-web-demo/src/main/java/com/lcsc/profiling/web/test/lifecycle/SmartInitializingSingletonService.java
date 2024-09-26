package com.lcsc.profiling.web.test.lifecycle;

import com.lcsc.profiling.web.annotation.Test;
import lombok.SneakyThrows;
import org.springframework.beans.factory.SmartInitializingSingleton;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-26 00:29
 */
@Test
public class SmartInitializingSingletonService implements SmartInitializingSingleton {

    @SneakyThrows
    @Override
    public void afterSingletonsInstantiated() {
        Thread.sleep(777);
    }

}
