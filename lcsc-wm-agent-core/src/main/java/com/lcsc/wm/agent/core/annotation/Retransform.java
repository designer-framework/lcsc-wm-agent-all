package com.lcsc.wm.agent.core.annotation;

import com.lcsc.wm.agent.core.configuration.instrument.InstrumentationBeanDefinitionRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Import({InstrumentationBeanDefinitionRegistrar.class})
public @interface Retransform {

    /**
     * <p>
     * {@link com.alibaba.bytekit.agent.inst.Instrument 类上必须添加该注解}
     */
    Class<?> value();

}
