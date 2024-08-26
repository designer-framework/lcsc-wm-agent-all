package com.lcsc.wm.agent.plugin.core.configuration;

import com.lcsc.wm.agent.core.configuration.lifecycle.AgentLifeCycleAutoConfiguration;
import com.lcsc.wm.agent.core.flamegraph.FlameGraph;
import com.lcsc.wm.agent.core.properties.AgentOutputProperties;
import com.lcsc.wm.agent.plugin.core.analysis.hook.StartReporterServerHook;
import com.lcsc.wm.agent.plugin.core.analysis.hook.WriteFlameGraphHtmlResourceHook;
import com.lcsc.wm.agent.plugin.core.analysis.hook.server.HandlerMapping;
import com.lcsc.wm.agent.plugin.core.analysis.hook.server.MethodHandlerMapping;
import com.lcsc.wm.agent.plugin.core.analysis.statistics.*;
import com.lcsc.wm.agent.plugin.core.properties.AgentServerProperties;
import com.lcsc.wm.agent.plugin.core.utils.SpringAgentResourcesHandler;
import com.lcsc.wm.agent.plugin.core.vo.SpringAgentStatistics;
import com.lcsc.wm.agent.plugin.core.vo.SpringAgentStatisticsVO;
import com.lcsc.wm.agent.plugin.core.vo.SpringBootApplicationInfo;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.instrument.Instrumentation;
import java.util.List;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-24 23:29
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(AgentServerProperties.class)
@AutoConfigureBefore(AgentLifeCycleAutoConfiguration.class)
public class SpringApplicationReporterAutoConfiguration {

    @Bean
    HandlerMapping handlerMapping() {
        return new MethodHandlerMapping();
    }

    @Bean
    StartReporterServerHook startReporterServerHook(AgentServerProperties agentServerProperties, HandlerMapping handlerMapping) {
        return new StartReporterServerHook(agentServerProperties, handlerMapping);
    }

    @Bean
    WriteFlameGraphHtmlResourceHook writeStartUpAnalysisHtmlHook(
            SpringAgentResourcesHandler springAgentResourcesHandler
            , StatisticsAggregation statisticsAggregation
            , FlameGraph flameGraph
    ) {
        return new WriteFlameGraphHtmlResourceHook(springAgentResourcesHandler, statisticsAggregation, flameGraph);
    }

    @Bean
    SpringAgentResourcesHandler springAgentResourcesHandler(AgentOutputProperties agentOutputProperties) {
        return new SpringAgentResourcesHandler(agentOutputProperties);
    }

    @Bean
    SpringApplicationStatisticsAggregation springApplicationStatisticsAggregation(SpringAgentStatistics springAgentStatistics, List<StatisticsBuilder> statisticsBuilders) {
        return new SpringApplicationStatisticsAggregation(springAgentStatistics, statisticsBuilders);
    }

    @Bean
    SpringAgentStatistics springAgentStatisticsVO() {
        return new SpringAgentStatisticsVO();
    }

    @Bean
    StatisticsBuilder startUpLabelStatisticsBuilder(SpringBootApplicationInfo springBootApplicationInfo) {
        return new StartUpLabelStatisticsBuilder(springBootApplicationInfo);
    }

    @Bean
    StatisticsBuilder createdBeansStatisticsBuilder() {
        return new CreatedBeansStatisticsBuilder();
    }

    @Bean
    StatisticsBuilder methodInvokeMetricsStatisticsBuilder() {
        return new MethodInvokeMetricsStatisticsBuilder();
    }

    @Bean
    StatisticsBuilder classLoaderLoadJarStatisticsBuilder(Instrumentation instrumentation) {
        return new ClassLoaderLoadJarStatisticsBuilder(instrumentation);
    }

    @Bean
    StatisticsBuilder componentsMetricStatisticsBuilder() {
        return new ComponentsMetricStatisticsBuilder();
    }

}
