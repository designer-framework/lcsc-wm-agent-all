package com.lcsc.wm.agent.plugin.core.vo;

import lombok.Data;
import org.springframework.beans.factory.support.RootBeanDefinition;


/**
 * 该方法耗时包含了 this#afterPropertiesSet和this#aopProxy 的耗时, 但是不包含 this#afterSingletonsInstantiated的耗时
 *
 * @see org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean(String, RootBeanDefinition, Object[])
 */
@Data
public class BeanLifeCycleVO {

    /**
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    private BeanLifeCycleDuration afterPropertiesSet;

    /**
     * @see org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#wrapIfNecessary(Object, String, Object)
     */
    private BeanLifeCycleDuration createAopProxyClass;

    /**
     * @see org.springframework.beans.factory.SmartInitializingSingleton#afterSingletonsInstantiated()
     */
    private BeanLifeCycleDuration afterSingletonsInstantiated;

}
