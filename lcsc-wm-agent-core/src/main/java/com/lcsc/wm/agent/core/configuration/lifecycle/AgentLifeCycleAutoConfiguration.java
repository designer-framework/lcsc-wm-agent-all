package com.lcsc.wm.agent.core.configuration.lifecycle;

import com.lcsc.wm.agent.framework.state.AgentState;
import com.lcsc.wm.agent.framework.state.SimpleAgentState;
import com.lcsc.wm.agent.core.hook.AgentLifeCycleStopHook;
import com.lcsc.wm.agent.core.hook.FlameGraphAgentLifeCycleHook;
import com.lcsc.wm.agent.core.lifecycle.AgentLifeCycleHook;
import com.lcsc.wm.agent.core.lifecycle.SimpleAgentLifeCycle;
import com.lcsc.wm.agent.core.properties.AgentFlameGraphProperties;
import com.lcsc.wm.agent.core.vo.AgentStatistics;
import com.lcsc.wm.agent.core.vo.AgentStatisticsVO;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-24 23:29
 */
@Configuration
public class AgentLifeCycleAutoConfiguration {

    @Bean
    SimpleAgentState simpleAgentState() {
        return new SimpleAgentState();
    }

    @Bean
    SimpleAgentLifeCycle simpleAgentLifeCycle(List<AgentLifeCycleHook> agentLifeCycleHooks, AgentState agentState) {
        return new SimpleAgentLifeCycle(agentLifeCycleHooks, agentState);
    }

    @Bean
    AgentLifeCycleStopHook agentLifeCycleStopHook() {
        return new AgentLifeCycleStopHook();
    }

    @Bean
    @ConditionalOnMissingBean
    AgentStatistics agentStatisticsVO() {
        return new AgentStatisticsVO();
    }

    @Bean
    FlameGraphAgentLifeCycleHook flameGraphAgentLifeCycleHook(AgentFlameGraphProperties agentFlameGraphProperties, AgentStatistics agentStatistics) {
        return new FlameGraphAgentLifeCycleHook(agentFlameGraphProperties, agentStatistics);
    }

}
