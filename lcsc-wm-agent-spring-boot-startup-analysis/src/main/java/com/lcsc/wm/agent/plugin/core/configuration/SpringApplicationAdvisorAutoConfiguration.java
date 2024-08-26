package com.lcsc.wm.agent.plugin.core.configuration;

import com.lcsc.wm.agent.framework.lifecycle.AgentLifeCycle;
import com.lcsc.wm.agent.framework.vo.ClassMethodInfo;
import com.lcsc.wm.agent.plugin.core.analysis.lifecycle.SpringApplicationLifeCyclePointcutAdvisor;
import com.lcsc.wm.agent.plugin.core.vo.SpringAgentStatistics;
import com.lcsc.wm.agent.plugin.core.vo.SpringBootApplicationInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class SpringApplicationAdvisorAutoConfiguration {

    @Bean
    SpringApplicationLifeCyclePointcutAdvisor springApplicationLifeCyclePointcutAdvisor(
            AgentLifeCycle agentLifeCycles, SpringAgentStatistics springAgentStatistics
            , SpringBootApplicationInfo springBootApplicationInfo
    ) {
        return new SpringApplicationLifeCyclePointcutAdvisor(
                ClassMethodInfo.create("org.springframework.boot.SpringApplication#run(java.lang.Class, java.lang.String[])")
                , agentLifeCycles, springAgentStatistics, springBootApplicationInfo
        );
    }

    @Bean
    SpringBootApplicationInfo springBootApplicationInfo() {
        return new SpringBootApplicationInfo();
    }

}
