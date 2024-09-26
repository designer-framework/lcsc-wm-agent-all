package com.lcsc.wm.agent.plugin.core.configuration;

import com.lcsc.wm.agent.core.interceptor.SimpleSpyInterceptorApi;
import com.lcsc.wm.agent.framework.vo.ClassMethodInfo;
import com.lcsc.wm.agent.plugin.core.analysis.bean.InitializingSingletonsPointcutAdvisor;
import com.lcsc.wm.agent.plugin.core.analysis.bean.SpringBeanAopProxyPointcutAdvisor;
import com.lcsc.wm.agent.plugin.core.analysis.bean.SpringInitAnnotationBeanPointcutAdvisor;
import com.lcsc.wm.agent.plugin.core.analysis.bean.SpringInitializingBeanPointcutAdvisor;
import com.lcsc.wm.agent.plugin.core.analysis.component.*;
import com.lcsc.wm.agent.plugin.core.enums.SpringComponentEnum;
import com.lcsc.wm.agent.plugin.core.vo.SpringAgentStatistics;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * list.{ ?#this == 99999}
 *
 * @description:
 * @author: Designer
 * @date : 2024-07-26 23:04
 */
@Configuration(proxyBeanMethods = false)
public class SpringComponentMethodInvokeAutoConfiguration {

    @Bean
    ComponentInitializedListener componentInitializedListener(SpringAgentStatistics springAgentStatistics) {
        return new ComponentInitializedListener(springAgentStatistics);
    }

    /**
     * FeignClient耗时
     *
     * @see org.springframework.cloud.openfeign.FeignClientFactoryBean#getTarget(), getObject()
     */
    @Bean
    FeignClientsCreatorPointcutAdvisor feignClientsCreatorPointcutAdvisor() {
        return new FeignClientsCreatorPointcutAdvisor(
                SpringComponentEnum.FEIGN_CLIENT_FACTORY_BEAN
                , ClassMethodInfo.create("org.springframework.cloud.openfeign.FeignClientFactoryBean#getTarget()")
                , FeignClientsCreatorPointcutAdvisor.FeignClientSpyInterceptorApi.class
        );
    }

    /**
     * @return
     * @see springfox.documentation.spring.web.plugins.DocumentationPluginsBootstrapper#start()
     */
    @Bean
    SwaggerCreatorPointcutAdvisor swaggerCreatorPointcutAdvisor() {
        return new SwaggerCreatorPointcutAdvisor(
                SpringComponentEnum.SWAGGER
                , ClassMethodInfo.create("springfox.documentation.spring.web.plugins.DocumentationPluginsBootstrapper#start()")
                , SimpleSpyInterceptorApi.class
        );
    }

    /**
     * Apollo配置加载耗时
     *
     * @return
     * @see com.ctrip.framework.apollo.spring.boot.ApolloApplicationContextInitializer#initialize(ConfigurableEnvironment)
     */
    @Bean
    @ConditionalOnMissingBean(ApolloCreatorPointcutAdvisor.class)
    ApolloCreatorPointcutAdvisor apolloCreatorPointcutAdvisor() {
        return new ApolloCreatorPointcutAdvisor(
                SpringComponentEnum.APOLLO_APPLICATION_CONTEXT_INITIALIZER
                , ClassMethodInfo.create("com.ctrip.framework.apollo.spring.boot.ApolloApplicationContextInitializer#initialize(org.springframework.core.env.ConfigurableEnvironment)")
        );
    }

    /**
     * Apollo配置加载耗时
     *
     * @return
     * @see com.ctrip.framework.apollo.ConfigService#getConfig(java.lang.String)
     */
    @Bean
    ApolloLoadNamespacePointcutAdvisor apolloLoadNamespacePointcutAdvisor() {
        return new ApolloLoadNamespacePointcutAdvisor(
                SpringComponentEnum.APOLLO_APPLICATION_CONTEXT_INITIALIZER
                , ClassMethodInfo.create("com.ctrip.framework.apollo.ConfigService#getConfig(java.lang.String)")
        );
    }

    /**
     * 扫包耗时
     *
     * @return
     * @see org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider#findCandidateComponents(java.lang.String)
     */
    @Bean
    ClassPathScanningCandidateComponentPointcutAdvisor classPathScanningCandidateComponentPointcutAdvisor(SpringAgentStatistics springAgentStatistics) {
        return new ClassPathScanningCandidateComponentPointcutAdvisor(
                SpringComponentEnum.CLASS_PATH_SCANNING_CANDIDATE
                , ClassMethodInfo.create("org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider#findCandidateComponents(java.lang.String)")
                , springAgentStatistics
        );
    }

    /**
     * @see org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#wrapIfNecessary(java.lang.Object, java.lang.String, java.lang.Object)
     */
    @Bean
    public SpringBeanAopProxyPointcutAdvisor springBeanAopProxyPointcutAdvisor() {
        return new SpringBeanAopProxyPointcutAdvisor(
                SpringComponentEnum.ABSTRACT_AUTO_PROXY_CREATOR,
                ClassMethodInfo.create("org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#wrapIfNecessary(java.lang.Object, java.lang.String, java.lang.Object)")
        );
    }

    /**
     * @see javax.annotation.PostConstruct
     * @see org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor#postProcessBeforeInitialization(java.lang.Object, java.lang.String)
     * @see org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor.LifecycleMetadata#invokeInitMethods(java.lang.Object, java.lang.String)
     */
    @Bean
    public SpringInitAnnotationBeanPointcutAdvisor springInitAnnotationBeanPointcutAdvisor() {
        return new SpringInitAnnotationBeanPointcutAdvisor(
                SpringComponentEnum.POST_CONSTRUCT_BEAN
                , ClassMethodInfo.create("org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor$LifecycleMetadata#invokeInitMethods(java.lang.Object, java.lang.String)")
                , SpringInitAnnotationBeanPointcutAdvisor.InitMethodSpyInterceptorApi.class
        );
    }

    /**
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     * @see org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#invokeInitMethods(java.lang.String, java.lang.Object, org.springframework.beans.factory.support.RootBeanDefinition)
     */
    @Bean
    public SpringInitializingBeanPointcutAdvisor springInitializingBeanPointcutAdvisor() {
        return new SpringInitializingBeanPointcutAdvisor(
                SpringComponentEnum.INITIALIZING_BEAN
                //, ClassMethodInfo.create("org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#invokeInitMethods(java.lang.String, java.lang.Object, org.springframework.beans.factory.support.RootBeanDefinition)")
                , ClassMethodInfo.create("**#afterPropertiesSet()")
                , SpringInitializingBeanPointcutAdvisor.InitializingBeanSpyInterceptorApi.class
        );
    }

    @Bean
    public InitializingSingletonsPointcutAdvisor initializingSingletonsPointcutAdvisor() {
        return new InitializingSingletonsPointcutAdvisor(
                SpringComponentEnum.SMART_INITIALIZING_SINGLETON
                , ClassMethodInfo.create("org.springframework.beans.factory.support.DefaultListableBeanFactory#preInstantiateSingletons()")
                , InitializingSingletonsPointcutAdvisor.AfterSingletonsInstantiatedSpyInterceptorApi.class
        );
    }

}
