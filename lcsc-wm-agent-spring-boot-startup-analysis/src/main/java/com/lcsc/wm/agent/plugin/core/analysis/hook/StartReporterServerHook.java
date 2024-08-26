package com.lcsc.wm.agent.plugin.core.analysis.hook;

import com.lcsc.wm.agent.plugin.core.analysis.hook.server.DispatcherServer;
import com.lcsc.wm.agent.plugin.core.analysis.hook.server.HandlerMapping;
import com.lcsc.wm.agent.plugin.core.properties.AgentServerProperties;
import com.lcsc.wm.agent.core.constants.LifeCycleStopHookOrdered;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.Ordered;

@Slf4j
public class StartReporterServerHook implements DisposableBean, Ordered {

    private final AgentServerProperties agentServerProperties;

    private final HandlerMapping handlerMapping;

    public StartReporterServerHook(AgentServerProperties agentServerProperties, HandlerMapping handlerMapping) {
        this.agentServerProperties = agentServerProperties;
        this.handlerMapping = handlerMapping;
    }

    /**
     * 异步启动性能分析报表Web服务端
     */
    @Override
    public void destroy() throws Exception {
        DispatcherServer dispatcherServer = new DispatcherServer(agentServerProperties.getPort(), handlerMapping);
        dispatcherServer.start();
    }

    @Override
    public int getOrder() {
        return LifeCycleStopHookOrdered.START_REPORTER_SERVER;
    }

}
