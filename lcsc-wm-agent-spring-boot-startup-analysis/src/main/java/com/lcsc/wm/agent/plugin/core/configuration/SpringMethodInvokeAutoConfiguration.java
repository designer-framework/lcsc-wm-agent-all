package com.lcsc.wm.agent.plugin.core.configuration;

import com.lcsc.wm.agent.core.annotation.EnabledMethodInvokeWatch;
import com.lcsc.wm.agent.core.annotation.MethodInvokeWatch;
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
@EnabledMethodInvokeWatch({
        //spring-boot3.2.0+= 类加载器
        @MethodInvokeWatch("org.springframework.boot.loader.launch.LaunchedClassloader#loadClass(java.lang.String, boolean)"),
        //spring-boot3.2.0- 类加载器
        @MethodInvokeWatch("org.springframework.boot.loader.LaunchedURLClassLoader#loadClass(java.lang.String, boolean)"),
})
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
