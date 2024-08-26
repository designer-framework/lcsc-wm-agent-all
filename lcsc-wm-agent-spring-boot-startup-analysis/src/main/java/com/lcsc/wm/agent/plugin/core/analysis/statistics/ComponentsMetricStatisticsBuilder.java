package com.lcsc.wm.agent.plugin.core.analysis.statistics;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.lcsc.wm.agent.plugin.core.enums.SpringComponentEnum;
import com.lcsc.wm.agent.plugin.core.enums.StatisticsEnum;
import com.lcsc.wm.agent.plugin.core.vo.InitializedComponent;
import com.lcsc.wm.agent.plugin.core.vo.InitializedComponentsMetric;
import com.lcsc.wm.agent.plugin.core.vo.SpringAgentStatistics;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * @description:
 * @author: Designer
 * @date : 2024-08-04 18:23
 */
public class ComponentsMetricStatisticsBuilder implements StatisticsBuilder {

    @Override
    public Object build(SpringAgentStatistics springAgentStatistics) {
        return getComponentsMetric(springAgentStatistics);
    }

    @Override
    public boolean support(String statisticsType) {
        return StatisticsEnum.componentsMetric.getType().equals(statisticsType);
    }

    private InitializedComponentsMetric getComponentsMetric(SpringAgentStatistics statistics) {
        //构建报表
        InitializedComponentsMetric rootMetric = createRootMetric(SpringComponentEnum.SPRING_APPLICATION, statistics.getAgentTime());

        //各组件耗时统计, 如: Apollo, Swagger
        Collection<InitializedComponent> initializedComponents = statistics.getInitializedComponents();
        initializedComponents.forEach(initializedComponent -> {
            if (initializedComponent.isLazyRoot()) {
                initializedComponent.updateDurationByChildren();
            }
        });
        rootMetric.addChildren(JSONObject.parseObject(
                JSON.toJSONString(initializedComponents), new TypeReference<List<InitializedComponentsMetric>>() {
                }
        ));

        return fillComponentMetric(rootMetric, true);
    }

    /**
     * 组件根节点
     *
     * @param showName
     * @param duration
     * @return
     */
    public InitializedComponentsMetric createRootMetric(SpringComponentEnum showName, BigDecimal duration) {
        return new InitializedComponentsMetric(showName.getComponentName(), showName.getDisplayName(), duration);
    }

    /**
     * SpringBean-Aop耗时统计
     *
     * @return
     */

    public InitializedComponentsMetric fillComponentMetric(InitializedComponentsMetric componentsMetric, boolean isRootMetric) {
        //根节点
        if (isRootMetric) {
            componentsMetric.fillOthersDuration();
        }

        List<InitializedComponentsMetric> children = componentsMetric.getChildren();
        if (!CollectionUtils.isEmpty(children)) {

            //计算耗时百分比
            calcPercentage(children);

            //排序
            children.sort(Comparator.comparing(InitializedComponentsMetric::getDuration).reversed());

            for (InitializedComponentsMetric child : children) {

                //有组件明细
                if (!CollectionUtils.isEmpty(child.getChildren())) {

                    //明细耗时统计
                    //child.fillOthersDuration();

                    //遍历tree
                    fillComponentMetric(child, false);

                }

            }

        }

        return componentsMetric;
    }

    private void calcPercentage(List<InitializedComponentsMetric> children) {
        BigDecimal remaining = BigDecimal.ONE;
        BigDecimal sum = children.stream().map(InitializedComponentsMetric::getDuration).reduce(BigDecimal.ZERO, BigDecimal::add);
        for (InitializedComponentsMetric child : children) {

            //还有剩余
            if (remaining.compareTo(BigDecimal.ZERO) > 0) {

                BigDecimal percent = child.getDuration().divide(sum, 3, RoundingMode.HALF_UP);
                child.setPercent(percent.multiply(BigDecimal.valueOf(100)));
                remaining = remaining.subtract(percent);

            } else {

                child.setPercent(BigDecimal.ZERO);

            }
        }

    }

}
