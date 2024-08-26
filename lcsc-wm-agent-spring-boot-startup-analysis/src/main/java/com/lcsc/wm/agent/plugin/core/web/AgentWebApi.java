package com.lcsc.wm.agent.plugin.core.web;

import com.lcsc.wm.agent.plugin.core.analysis.statistics.StatisticsAggregation;
import com.lcsc.wm.agent.plugin.core.annotation.WebController;
import com.lcsc.wm.agent.plugin.core.annotation.WebMapping;
import com.lcsc.wm.agent.plugin.core.enums.StatisticsEnum;
import com.lcsc.wm.agent.plugin.core.utils.SpringAgentResourcesHandler;
import com.sun.net.httpserver.HttpExchange;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.Collections;

@WebController
public class AgentWebApi {

    @Autowired
    private StatisticsAggregation statisticsAggregation;

    @Autowired
    private SpringAgentResourcesHandler springAgentResourcesHandler;

    /**
     * 报表首页
     *
     * @return
     */
    @WebMapping({"/"})
    public String springAgentAnalysisServer() {
        return "Spring Agent Analysis Server";
    }

    /**
     * 火焰图
     *
     * @param exchange
     * @return
     */
    @WebMapping({"/*.html"})
    public String flameGraph(HttpExchange exchange) {
        return springAgentResourcesHandler.readOutputResourrceToString(exchange.getRequestURI().getPath());
    }

    /**
     * 静态资源
     *
     * @param exchange
     * @return
     */
    @WebMapping({"/*.js", "/*.icon", "/*.css"})
    public String staticResources(HttpExchange exchange) {
        return springAgentResourcesHandler.resourrceToString(exchange.getRequestURI().getPath());
    }

    /**
     * 报表统计数据
     *
     * @return
     */
    @WebMapping(value = {"/analysis/json"}, contentType = "application/json; charset=utf-8")
    public Object springAgentStatisticsVO(AnalysisJsonVO analysisJsonVO) {
        //
        if (StringUtils.isEmpty(analysisJsonVO.type)) {

            return statisticsAggregation.statisticsAggregation();

            //
        } else {

            Object statistics = statisticsAggregation.statistics(analysisJsonVO.type);
            if (statistics != null) {
                return statistics;
            } else {
                return Collections.emptyMap();
            }

        }

    }

    @Data
    static class AnalysisJsonVO {
        private StatisticsEnum type;
    }

}
