package com.lcsc.wm.agent.core.configuration;

import com.lcsc.wm.agent.core.spy.CompositeSpyAPI;
import com.lcsc.wm.agent.core.spy.SpyExtensionApiImpl;
import com.lcsc.wm.agent.framework.advisor.PointcutAdvisor;
import com.lcsc.wm.agent.framework.spy.SpyExtensionApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.agent.SpyAPI;
import java.lang.instrument.Instrumentation;
import java.util.List;

/**
 * @see com.lcsc.wm.agent.core.configuration.env.AgentPropertiesAutoConfiguration
 * @see com.lcsc.wm.agent.core.configuration.lifecycle.AgentLifeCycleAutoConfiguration
 */
@Configuration(proxyBeanMethods = false)
public class AgentAutoConfiguration {

    public static final String INSTRUMENTATION = "com.lcsc.wm.agent.core.configuration.commonInstrumentation";

    @Bean(name = INSTRUMENTATION)
    Instrumentation instrumentation(Environment environment) {
        return environment.getRequiredProperty(INSTRUMENTATION, Instrumentation.class);
    }

    @Bean
    SpyAPI.AbstractSpy abstractSpy(List<SpyExtensionApi> spyExtensionApis) {
        return new CompositeSpyAPI(spyExtensionApis);
    }

    @Bean
    SpyExtensionApi spyExtensionApi(List<PointcutAdvisor> pointcutAdvisors) {
        return new SpyExtensionApiImpl(pointcutAdvisors);
    }

}
