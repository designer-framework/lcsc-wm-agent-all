package com.lcsc.wm.agent.core.configuration.advisor;

import com.lcsc.wm.agent.core.advisor.SimpleMethodInvokePointcutAdvisor;
import com.lcsc.wm.agent.core.annotation.EnabledMethodInvokeWatch;
import com.lcsc.wm.agent.core.annotation.MethodInvokeWatch;
import com.lcsc.wm.agent.core.properties.MethodInvokeAdvisorProperties;
import com.lcsc.wm.agent.framework.interceptor.SpyInterceptorApi;
import lombok.Setter;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Arrays;
import java.util.Collection;

@Setter
public class AgentMethodInvokeImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    private Environment environment;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        //
        if (Boolean.parseBoolean(environment.resolvePlaceholders(BeanDefinitionRegistryUtils.ENABLED))) {
            return;
        }

        annotationMetadata.getAnnotations().stream(EnabledMethodInvokeWatch.class)
                .map(annotation -> Arrays.asList(annotation.getAnnotationArray("value", MethodInvokeWatch.class)))
                .flatMap(Collection::stream)
                .forEach(methodInvokeWatch -> {

                    BeanDefinitionRegistryUtils.registry(registry
                            , new MethodInvokeAdvisorProperties(
                                    (Class<? extends SimpleMethodInvokePointcutAdvisor>) methodInvokeWatch.getClass("pointcutAdvisor")
                                    , methodInvokeWatch.getString("value")
                                    , methodInvokeWatch.getBoolean("canRetransform")
                                    , (Class<? extends SpyInterceptorApi>) methodInvokeWatch.getClass("interceptor")
                            )
                    );

                });

    }

}
