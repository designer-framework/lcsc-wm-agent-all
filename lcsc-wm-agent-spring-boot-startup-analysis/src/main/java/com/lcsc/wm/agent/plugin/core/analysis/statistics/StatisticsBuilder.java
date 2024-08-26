package com.lcsc.wm.agent.plugin.core.analysis.statistics;

import com.lcsc.wm.agent.plugin.core.vo.SpringAgentStatistics;

/**
 * @description:
 * @author: Designer
 * @date : 2024-08-04 17:58
 */
public interface StatisticsBuilder {

    Object build(SpringAgentStatistics springAgentStatistics);

    boolean support(String statisticsType);

}
