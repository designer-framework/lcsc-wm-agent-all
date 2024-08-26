package com.lcsc.wm.agent.plugin.core.enums;

public enum SpringBeanLifeCycleEnum {

    /**
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    AfterPropertiesSet,
    /**
     * @see org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#wrapIfNecessary(Object, String, Object)
     */
    CreateAopProxyClass,
    /**
     * @see org.springframework.beans.factory.SmartInitializingSingleton#afterSingletonsInstantiated()
     */
    AfterSingletonsInstantiated,
    /**
     * 其它不便统计的耗时
     */
    Others;

}
