package com.lcsc.wm.agent.core.configuration.instrument;

import com.lcsc.wm.agent.core.annotation.EnabledInstrument;
import com.lcsc.wm.agent.core.annotation.Retransform;
import com.lcsc.wm.agent.core.configuration.AgentAutoConfiguration;
import com.lcsc.wm.agent.core.hook.InstrumentLifeCycleHook;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class InstrumentationBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        List<RetransformAttribute> retransformAttributes = annotationMetadata.getAnnotations().stream(EnabledInstrument.class)
                .map(annotation -> Arrays.asList(annotation.getAnnotationArray("value", Retransform.class)))
                .flatMap(Collection::stream)
                .map(retransformMergedAnnotation -> {

                    RetransformAttribute retransformAttribute = new RetransformAttribute();
                    retransformAttribute.setInstrumentClass(retransformMergedAnnotation.getClass("value"));
                    return retransformAttribute;

                }).collect(Collectors.toList());

        registry(registry, retransformAttributes);
    }

    public void registry(BeanDefinitionRegistry registry, List<RetransformAttribute> retransformAttributes) {
        BeanDefinitionBuilder definitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(InstrumentLifeCycleHook.class);
        definitionBuilder.addConstructorArgValue(retransformAttributes);
        definitionBuilder.addPropertyReference("instrumentation", AgentAutoConfiguration.INSTRUMENTATION);
        //
        String beanName = InstrumentLifeCycleHook.class.getSimpleName() + "." + System.nanoTime();
        registry.registerBeanDefinition(beanName, definitionBuilder.getBeanDefinition());
    }

}
