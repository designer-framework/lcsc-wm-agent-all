package com.lcsc.wm.agent.plugin.core.analysis.statistics;

import com.lcsc.wm.agent.plugin.core.enums.StatisticsType;

import java.util.Map;

/**
 * @description:
 * @author: Designer
 * @date : 2024-08-04 18:43
 */
public interface StatisticsAggregation {

    Map<String, Object> statisticsAggregation();

    Object statistics(StatisticsType statisticsType);

}
