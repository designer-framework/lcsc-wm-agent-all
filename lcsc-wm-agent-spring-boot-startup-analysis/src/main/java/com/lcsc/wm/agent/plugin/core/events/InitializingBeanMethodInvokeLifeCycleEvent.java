package com.lcsc.wm.agent.plugin.core.events;

import com.lcsc.wm.agent.plugin.core.vo.BeanLifeCycleDuration;
import lombok.Getter;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-14 12:28
 */
@Getter
public class InitializingBeanMethodInvokeLifeCycleEvent extends BeanCreationLifeCycleEvent {

    public InitializingBeanMethodInvokeLifeCycleEvent(Object source, String beanName, BeanLifeCycleDuration lifeCycleDurations) {
        super(source, beanName, lifeCycleDurations);
    }

}
