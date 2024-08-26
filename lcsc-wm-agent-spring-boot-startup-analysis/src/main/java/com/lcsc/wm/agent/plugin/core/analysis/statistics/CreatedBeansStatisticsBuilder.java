package com.lcsc.wm.agent.plugin.core.analysis.statistics;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.lcsc.wm.agent.plugin.core.enums.StatisticsEnum;
import com.lcsc.wm.agent.plugin.core.vo.SpringAgentStatistics;

/**
 * @description:
 * @author: Designer
 * @date : 2024-08-04 18:23
 */
public class CreatedBeansStatisticsBuilder implements StatisticsBuilder {

    @Override
    public Object build(SpringAgentStatistics springAgentStatistics) {
        JSONArray jsonArray = (JSONArray) JSON.toJSON(springAgentStatistics.getCreatedBeans());
        /*for (int i = 0; i < jsonArray.size(); i++) {
            ((Map) jsonArray.get(i)).put("isRoot", true);
        }*/

        return jsonArray;
    }

    @Override
    public boolean support(String statisticsType) {
        return StatisticsEnum.beanInitResultList.getType().equals(statisticsType);
    }

}
