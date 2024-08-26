package com.lcsc.wm.agent.plugin.core.analysis.bean;

import com.lcsc.wm.agent.core.lifecycle.AgentLifeCycleHook;
import com.lcsc.wm.agent.core.vo.MethodInvokeVO;
import com.lcsc.wm.agent.framework.vo.ClassMethodInfo;
import com.lcsc.wm.agent.framework.vo.InvokeVO;
import com.lcsc.wm.agent.plugin.core.analysis.component.AbstractComponentChildCreatorPointcutAdvisor;
import com.lcsc.wm.agent.plugin.core.enums.ComponentEnum;
import com.lcsc.wm.agent.plugin.core.enums.SpringComponentEnum;
import com.lcsc.wm.agent.plugin.core.events.BeanAopProxyCreatedLifeCycleEvent;
import com.lcsc.wm.agent.plugin.core.events.ComponentRootInitializedEvent;
import com.lcsc.wm.agent.plugin.core.events.SpringBootApplicationScanClassesEvent;
import com.lcsc.wm.agent.plugin.core.utils.ClassPathScanningCandidateComponentProviderInvoke;
import com.lcsc.wm.agent.plugin.core.vo.BeanLifeCycleDuration;
import com.lcsc.wm.agent.plugin.core.vo.InitializedComponent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @see org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#wrapIfNecessary(java.lang.Object, java.lang.String, java.lang.Object)
 */
@Slf4j
public class SpringBeanAopProxyPointcutAdvisor extends AbstractComponentChildCreatorPointcutAdvisor implements AgentLifeCycleHook, DisposableBean, InitializingBean {

    public SpringBeanAopProxyPointcutAdvisor(ComponentEnum componentEnum, ClassMethodInfo classMethodInfo) {
        super(componentEnum, classMethodInfo);
    }

    @Override
    public void start() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        InitializedComponent root = InitializedComponent.root(SpringComponentEnum.ABSTRACT_AUTO_PROXY_CREATOR, BigDecimal.ZERO, true);

        applicationEventPublisher.publishEvent(new ComponentRootInitializedEvent(this, root));

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        CompletableFuture.runAsync(() -> {

            try {
                for (StackTraceElement stack : stackTrace) {
                    if ("main".equals(stack.getMethodName())) {

                        //
                        String aopDesc = ClassPathScanningCandidateComponentProviderInvoke
                                .scan(contextClassLoader, false, Collections.singletonList("org.aspectj.lang.annotation.Aspect"), findBasePackages(stack.getClassName()))
                                .stream()
                                .map(String::valueOf).collect(Collectors.joining("</br>"));
                        root.setDesc("</br> Aspectj Classes: </br> " + aopDesc);

                        //
                        try {
                            applicationEventPublisher.publishEvent(new SpringBootApplicationScanClassesEvent(
                                    this, ClassPathScanningCandidateComponentProviderInvoke.scan(contextClassLoader, findBasePackages(stack.getClassName()))
                            ));
                        } catch (Exception e) {
                            //ignore
                            log.info("Ignores scan package classes", e);
                        }

                        break;

                    }
                }

            } catch (Exception e) {
                //ignore
                log.info("Ignores scan @Aspectj classes", e);
            }
        });

    }

    /**
     * 创建Bean, 入栈
     *
     * @param invokeVO
     */
    @Override
    protected void atMethodInvokeBefore(InvokeVO invokeVO, MethodInvokeVO methodInvokeVO) {
        super.atMethodInvokeBefore(invokeVO, methodInvokeVO);
    }

    @Override
    protected void atMethodInvokeAfter(InvokeVO invokeVO, MethodInvokeVO methodInvokeVO) {
        //被AOP代理过才发布事件
        if (invokeVO.getReturnObj() != null && !(invokeVO.getReturnObj() == invokeVO.getParams()[0])) {
            super.atMethodInvokeAfter(invokeVO, methodInvokeVO);

            String beanName = childName(invokeVO);
            //统计耗时
            BeanLifeCycleDuration beanLifeCycleDuration = BeanLifeCycleDuration.create(beanName, methodInvokeVO);
            applicationEventPublisher.publishEvent(
                    new BeanAopProxyCreatedLifeCycleEvent(this, childName(invokeVO), beanLifeCycleDuration)
            );
        }
    }

    @Override
    protected String childName(InvokeVO invokeVO) {
        return String.valueOf(invokeVO.getParams()[1]);
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    /**
     *
     * @param
     * @return
     */
    @SneakyThrows
    private Set<String> findBasePackages(String springApplicationClassStr) {
        //
        Method findMergedAnnotationAttributesMethod = ClassUtils.getMethod(
                Class.forName("org.springframework.core.annotation.AnnotatedElementUtils", true, Thread.currentThread().getContextClassLoader())
                , "findMergedAnnotationAttributes"
                , AnnotatedElement.class, String.class, boolean.class, boolean.class
        );

        //
        Class<?> springApplicationClass = Class.forName(springApplicationClassStr, true, Thread.currentThread().getContextClassLoader());
        LinkedHashMap<String, Object> mergedAnnotationAttributes = (LinkedHashMap<String, Object>) ReflectionUtils.invokeMethod(
                findMergedAnnotationAttributesMethod, null
                , springApplicationClass, "org.springframework.context.annotation.ComponentScan", false, true
        );

        //
        return Arrays.stream((String[]) mergedAnnotationAttributes.get("basePackages"))
                .map(basePackage -> {
                    String[] splitPackage = basePackage.split("\\.");
                    return splitPackage.length > 1 ? splitPackage[0] + "." + splitPackage[1] : basePackage;
                }).collect(Collectors.toSet());
    }

}
