package com.lcsc.wm.agent.core.configuration.advisor;

import com.lcsc.wm.agent.core.advisor.SimpleMethodInvokePointcutAdvisor;
import com.lcsc.wm.agent.core.properties.AgentMethodInvokeProperties;
import com.lcsc.wm.agent.core.properties.MethodInvokeAdvisorProperties;
import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-08 01:49
 */
@Setter
public class AgentMethodInvokeRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {

    private Environment environment;

    /**
     * @param registry the bean definition registry used by the application context
     * @throws BeansException
     * @see SimpleMethodInvokePointcutAdvisor
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        if (Boolean.parseBoolean(environment.resolvePlaceholders(BeanDefinitionRegistryUtils.ENABLED))) {
            return;
        }

        Binder.get(environment)
                //将配置绑定到对象上
                .bind("spring.agent.trace", AgentMethodInvokeProperties.class)
                .ifBound(agentMethodInvokeProperties -> {

                    //将性能分析Bean的Definition注入到容器中
                    for (MethodInvokeAdvisorProperties advisorProperties : agentMethodInvokeProperties.getAdvisors()) {
                        BeanDefinitionRegistryUtils.registry(registry, advisorProperties);
                    }

                });

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

}
