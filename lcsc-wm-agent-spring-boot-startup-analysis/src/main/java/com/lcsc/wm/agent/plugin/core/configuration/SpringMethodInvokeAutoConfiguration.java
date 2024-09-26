package com.lcsc.wm.agent.plugin.core.configuration;

import com.lcsc.wm.agent.framework.vo.ClassMethodInfo;
import com.lcsc.wm.agent.plugin.core.analysis.bean.SpringBeanCreationPointcutAdvisor;
import com.lcsc.wm.agent.plugin.core.vo.SpringAgentStatistics;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-26 23:04
 */
@Configuration(proxyBeanMethods = false)
public class SpringMethodInvokeAutoConfiguration {

    /**
     * @see org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, java.lang.Object[])
     */
    @Bean
    public SpringBeanCreationPointcutAdvisor springBeanCreationPointcutAdvisor(SpringAgentStatistics springAgentStatistics) {
        return new SpringBeanCreationPointcutAdvisor(
                ClassMethodInfo.create("org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, java.lang.Object[])")
                , springAgentStatistics
        );
    }


}
