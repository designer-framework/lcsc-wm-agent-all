package com.lcsc.wm.agent.plugin.core.analysis.hook;

import com.lcsc.wm.agent.core.flamegraph.FlameGraph;
import com.lcsc.wm.agent.plugin.core.analysis.statistics.StatisticsAggregation;
import com.lcsc.wm.agent.plugin.core.utils.SpringAgentResourcesHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class WriteFlameGraphHtmlResourceHook implements DisposableBean {

    private static final String flameGraph_ = "/flame-graph.html";

    protected final StatisticsAggregation statisticsAggregation;

    protected final FlameGraph flameGraph;

    private final SpringAgentResourcesHandler springAgentResourcesHandler;

    public WriteFlameGraphHtmlResourceHook(
            SpringAgentResourcesHandler springAgentResourcesHandler
            , StatisticsAggregation statisticsAggregation
            , FlameGraph flameGraph
    ) {
        this.springAgentResourcesHandler = springAgentResourcesHandler;
        this.statisticsAggregation = statisticsAggregation;
        this.flameGraph = flameGraph;
    }

    @Override
    public void destroy() throws Exception {
        //导出火焰图
        CompletableFuture.runAsync(() -> {
            flameGraph.write(springAgentResourcesHandler.getOutputFile(flameGraph_));
        }).get();
    }

}
