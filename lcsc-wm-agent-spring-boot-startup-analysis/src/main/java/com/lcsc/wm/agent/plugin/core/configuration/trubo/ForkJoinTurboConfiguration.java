package com.lcsc.wm.agent.plugin.core.configuration.trubo;


import com.lcsc.wm.agent.plugin.core.analysis.hook.AgentForkJoinPoolHook;
import com.lcsc.wm.agent.plugin.core.annotation.ConditionalOnTurboPropCondition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnTurboPropCondition(pluginName = "fork-join")
public class ForkJoinTurboConfiguration {

    @Bean
    AgentForkJoinPoolHook agentForkJoinPoolHook() {
        return new AgentForkJoinPoolHook();
    }

}
