package com.lcsc.wm.agent.core.annotation;

import com.lcsc.wm.agent.core.configuration.advisor.AgentMethodInvokeImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(AgentMethodInvokeImportBeanDefinitionRegistrar.class)
public @interface EnabledMethodInvokeWatch {

    MethodInvokeWatch[] value();

}
