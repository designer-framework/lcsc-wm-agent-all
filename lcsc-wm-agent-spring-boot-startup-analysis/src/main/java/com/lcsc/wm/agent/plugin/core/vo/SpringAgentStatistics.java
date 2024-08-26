package com.lcsc.wm.agent.plugin.core.vo;

import com.lcsc.wm.agent.core.lifecycle.AgentLifeCycleHook;
import com.lcsc.wm.agent.core.vo.AgentStatistics;

import java.util.Collection;
import java.util.function.Consumer;

public interface SpringAgentStatistics extends AgentStatistics, AgentLifeCycleHook {

    void fillBeanCreate(String beanName, Consumer<SpringBeanVO> consumer);

    Collection<SpringBeanVO> getCreatedBeans();

    void addCreatedBean(SpringBeanVO springBeanVO);

    void addInitializedComponents(Collection<InitializedComponent> initializedComponents);

    Collection<InitializedComponent> getInitializedComponents();

}
