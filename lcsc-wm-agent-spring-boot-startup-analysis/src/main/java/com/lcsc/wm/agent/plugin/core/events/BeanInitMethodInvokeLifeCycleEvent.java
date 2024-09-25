package com.lcsc.wm.agent.plugin.core.events;

import com.lcsc.wm.agent.plugin.core.vo.BeanLifeCycleDuration;
import lombok.Getter;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-14 12:28
 */
@Getter
public class BeanInitMethodInvokeLifeCycleEvent extends BeanCreationLifeCycleEvent {

    /**
     * 0: PostConstruct注解
     * 1: InitializingBean
     */
    private final int type;

    public BeanInitMethodInvokeLifeCycleEvent(Object source, String beanName, BeanLifeCycleDuration lifeCycleDurations) {
        this(source, beanName, lifeCycleDurations, 0);
    }

    public BeanInitMethodInvokeLifeCycleEvent(Object source, String beanName, BeanLifeCycleDuration lifeCycleDurations, int type) {
        super(source, beanName, lifeCycleDurations);
        this.type = type;
    }

}
