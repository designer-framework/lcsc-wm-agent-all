package com.lcsc.wm.agent.plugin.core.enums;

public enum SpringBeanLifeCycleEnum {

    /**
     * @see org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#invokeInitMethods(java.lang.String, java.lang.Object, org.springframework.beans.factory.support.RootBeanDefinition)
     */
    AfterPropertiesSet,
    /**
     * @see org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor.LifecycleMetadata#invokeInitMethods(java.lang.Object, java.lang.String)
     */
    PostConstruct,
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
