package com.lcsc.wm.agent.plugin.core.analysis.statistics;

import com.lcsc.wm.agent.plugin.core.enums.StatisticsEnum;
import com.lcsc.wm.agent.plugin.core.vo.SpringAgentStatistics;
import com.lcsc.wm.agent.plugin.core.vo.SpringBootApplicationInfo;

/**
 * @description:
 * @author: Designer
 * @date : 2024-08-04 18:10
 */
public class StartUpLabelStatisticsBuilder implements StatisticsBuilder {

    private final SpringBootApplicationInfo springBootApplicationInfo;

    public StartUpLabelStatisticsBuilder(SpringBootApplicationInfo springBootApplicationInfo) {
        this.springBootApplicationInfo = springBootApplicationInfo;
    }

    /**
     * @param springAgentStatistics
     * @return
     * @see org.springframework.boot.web.servlet.context.AnnotationConfigServletWebApplicationContext
     */
    @Override
    public Object build(SpringAgentStatistics springAgentStatistics) {
        return springBootApplicationInfo.getSpringBootApplicationLabels();
    }

    @Override
    public boolean support(String statisticsType) {
        return StatisticsEnum.statisticsList.getType().equals(statisticsType);
    }

}
