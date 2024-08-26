package com.lcsc.wm.agent.core.configuration.env;

import com.lcsc.wm.agent.core.properties.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(AgentConfigProperties.class)
public class AgentPropertiesAutoConfiguration {

    @Bean
    public AgentOutputProperties agentOutputProperties(AgentConfigProperties agentConfigProperties) {
        return agentConfigProperties.getOutput();
    }

    @Bean
    public AgentMethodInvokeProperties agentMethodInvokeProperties(AgentConfigProperties agentConfigProperties) {
        return agentConfigProperties.getMethodInvoke();
    }

    @Bean
    public AgentFlameGraphProperties agentFlameGraphProperties(AgentConfigProperties agentConfigProperties) {
        return agentConfigProperties.getFlameGraph();
    }

    @Bean
    public AgentClassLoaderProperties agentClassLoaderProperties(AgentConfigProperties agentConfigProperties) {
        return agentConfigProperties.getClassLoaders();
    }

}
