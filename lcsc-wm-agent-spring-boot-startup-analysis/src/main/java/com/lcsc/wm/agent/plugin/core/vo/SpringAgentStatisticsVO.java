package com.lcsc.wm.agent.plugin.core.vo;

import com.lcsc.wm.agent.core.constants.LifeCycleOrdered;
import com.lcsc.wm.agent.core.vo.AgentStatisticsVO;
import com.lcsc.wm.agent.core.vo.DurationVO;
import com.lcsc.wm.agent.plugin.core.enums.SpringBeanLifeCycleEnum;
import lombok.Getter;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Consumer;

public class SpringAgentStatisticsVO extends AgentStatisticsVO implements SpringAgentStatistics {

    private final Map<String, SpringBeanVO> createdBeansMap = new ConcurrentHashMap<>();

    @Getter
    private final Collection<InitializedComponent> initializedComponents = new ConcurrentLinkedDeque<>();

    @Override
    public void fillBeanCreate(String beanName, Consumer<SpringBeanVO> consumer) {
        if (beanName != null) {
            consumer.accept(createdBeansMap.get(beanName));
        }
    }

    @Override
    public void addCreatedBean(SpringBeanVO springBeanVO) {
        createdBeansMap.put(springBeanVO.getName(), springBeanVO);
    }

    @Override
    public void addInitializedComponents(Collection<InitializedComponent> initializedComponents) {
        this.initializedComponents.addAll(initializedComponents);
    }


    @Override
    public Collection<SpringBeanVO> getCreatedBeans() {
        return createdBeansMap.values();
    }

    @Override
    public void stop() {
        createdBeansMap.values().forEach(springBeanVO -> {

            if (!CollectionUtils.isEmpty(springBeanVO.getBeanLifeCycles())) {

                BigDecimal sumDuration = springBeanVO.getBeanLifeCycles().values().stream()
                        .map(BeanLifeCycleDuration::getDuration).reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal othersDuration = springBeanVO.getActualDuration().subtract(sumDuration);
                //
                springBeanVO.addBeanLifeCycle(SpringBeanLifeCycleEnum.Others, BeanLifeCycleDuration.create("Others", new DurationVO().setDuration(othersDuration)));

            }

        });
    }

    @Override
    public int getOrder() {
        return LifeCycleOrdered.COLLECT_STATISTICS_DATA;
    }

}
