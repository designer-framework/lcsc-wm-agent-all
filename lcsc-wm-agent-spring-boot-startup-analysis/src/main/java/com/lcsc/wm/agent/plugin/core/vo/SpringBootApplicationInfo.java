package com.lcsc.wm.agent.plugin.core.vo;

import com.lcsc.wm.agent.core.lifecycle.AgentLifeCycleHook;
import com.lcsc.wm.agent.plugin.core.events.SpringBootApplicationLabelEvent;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

/**
 * @description:
 * @author: Designer
 * @date : 2024-08-21 22:37
 */
@Data
@Slf4j
public class SpringBootApplicationInfo implements ApplicationListener<SpringBootApplicationLabelEvent>, AgentLifeCycleHook {

    private ClassLoader classLoader;

    private Object springBootApplication;

    /**
     * Spring Boot Version
     */
    private List<SpringBootApplicationLabelVO> springBootApplicationLabels = new LinkedList<>();

    @Override
    public void onApplicationEvent(SpringBootApplicationLabelEvent event) {
        SpringBootApplicationLabelVO springBootApplicationLabelVO = event.getSpringBootApplicationLabelVO();
        if (springBootApplicationLabelVO != null && springBootApplicationLabelVO.isShow()) {
            springBootApplicationLabels.add(event.getSpringBootApplicationLabelVO());
        }
    }

    @Override
    public void stop() {
        AnnotationAwareOrderComparator.sort(springBootApplicationLabels);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    /**
     * 暂不开放
     *
     * @param methodName
     * @param methodArgTypes
     * @param methodConsumer
     * @return
     */
    Object invokeMethod(String methodName, String[] methodArgTypes, Function<Class<?>[], Object[]> methodConsumer) {

        try {
            Class<?>[] classes = new Class<?>[methodArgTypes.length];
            for (int i = 0; i < methodArgTypes.length; i++) {
                classes[i] = classLoader.loadClass(String.valueOf(methodArgTypes[i]));
            }

            Method method = springBootApplication.getClass().getMethod(methodName, classes);

            return method.invoke(springBootApplication, methodConsumer.apply(classes));
        } catch (Exception e) {
            log.error("_", e);
            return null;
        }

    }

}
