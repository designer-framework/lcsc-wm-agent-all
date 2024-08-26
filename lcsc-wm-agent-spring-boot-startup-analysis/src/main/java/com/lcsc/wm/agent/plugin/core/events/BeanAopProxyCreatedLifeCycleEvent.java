package com.lcsc.wm.agent.plugin.core.events;

import com.lcsc.wm.agent.plugin.core.vo.BeanLifeCycleDuration;
import lombok.Getter;

@Getter
public class BeanAopProxyCreatedLifeCycleEvent extends BeanCreationLifeCycleEvent {

    public BeanAopProxyCreatedLifeCycleEvent(Object source, String beanName, BeanLifeCycleDuration lifeCycleDurations) {
        super(source, beanName, lifeCycleDurations);
    }

}
