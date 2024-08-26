package com.lcsc.wm.agent.core.configuration.advisor;

import com.lcsc.wm.agent.core.advisor.SimpleMethodInvokePointcutAdvisor;
import com.lcsc.wm.agent.core.properties.MethodInvokeAdvisorProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-27 14:45
 */
@Slf4j
class BeanDefinitionRegistryUtils {

    public static final String ENABLED = "${spring.agent.flame-graph.high-precision}";

    public static void registry(BeanDefinitionRegistry registry, MethodInvokeAdvisorProperties advisorProperties) {
        String beanName = getBeanName(advisorProperties);

        if (registry.containsBeanDefinition(beanName)) {
            log.warn("Repeated method call statistics, ignored: {}", advisorProperties.getMethodInfo().getFullyQualifiedMethodName());
            return;
        }

        BeanDefinitionBuilder methodInvokeAdviceHandlerBuilder = BeanDefinitionBuilder.genericBeanDefinition(advisorProperties.getPointcutAdvisor());
        methodInvokeAdviceHandlerBuilder.addConstructorArgValue(advisorProperties.getMethodInfo());
        methodInvokeAdviceHandlerBuilder.addConstructorArgValue(advisorProperties.getCanRetransform());
        methodInvokeAdviceHandlerBuilder.addConstructorArgValue(advisorProperties.getInterceptor());
        //
        registry.registerBeanDefinition(beanName, methodInvokeAdviceHandlerBuilder.getBeanDefinition());
    }

    private static String getBeanName(MethodInvokeAdvisorProperties methodInvokeAdvisorProperties) {
        return SimpleMethodInvokePointcutAdvisor.class.getSimpleName() + "." + methodInvokeAdvisorProperties;
    }

}
