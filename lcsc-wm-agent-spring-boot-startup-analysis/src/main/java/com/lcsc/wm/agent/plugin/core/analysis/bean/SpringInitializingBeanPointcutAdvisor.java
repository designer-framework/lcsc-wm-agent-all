package com.lcsc.wm.agent.plugin.core.analysis.bean;

import com.alibaba.bytekit.asm.binding.Binding;
import com.alibaba.bytekit.asm.interceptor.annotation.AtEnter;
import com.alibaba.bytekit.asm.interceptor.annotation.AtExceptionExit;
import com.alibaba.bytekit.asm.interceptor.annotation.AtExit;
import com.lcsc.wm.agent.core.vo.MethodInvokeVO;
import com.lcsc.wm.agent.framework.interceptor.SpyInterceptorApi;
import com.lcsc.wm.agent.framework.vo.ClassMethodInfo;
import com.lcsc.wm.agent.framework.vo.InvokeVO;
import com.lcsc.wm.agent.plugin.core.analysis.component.AbstractComponentChildCreatorPointcutAdvisor;
import com.lcsc.wm.agent.plugin.core.enums.ComponentEnum;
import com.lcsc.wm.agent.plugin.core.events.BeanInitMethodInvokeLifeCycleEvent;
import com.lcsc.wm.agent.plugin.core.events.ComponentRootInitializedEvent;
import com.lcsc.wm.agent.plugin.core.vo.BeanLifeCycleDuration;
import com.lcsc.wm.agent.plugin.core.vo.InitializedComponent;
import lombok.SneakyThrows;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.agent.SpyAPI;
import java.math.BigDecimal;
import java.util.Collections;

/**
 * @see org.springframework.beans.factory.InitializingBean
 * @see org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#invokeInitMethods(java.lang.String, java.lang.Object, org.springframework.beans.factory.support.RootBeanDefinition)
 * @see
 */
public class SpringInitializingBeanPointcutAdvisor extends AbstractComponentChildCreatorPointcutAdvisor implements DisposableBean, InitializingBean {

    /**
     * @param componentEnum
     * @param classMethodInfo
     * @param interceptor
     * @see org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#invokeInitMethods(java.lang.String, java.lang.Object, org.springframework.beans.factory.support.RootBeanDefinition)
     */
    public SpringInitializingBeanPointcutAdvisor(ComponentEnum componentEnum, ClassMethodInfo classMethodInfo, Class<? extends SpyInterceptorApi> interceptor) {
        super(componentEnum, classMethodInfo, interceptor);
    }

    @Override
    public void start() {
        super.start();
        applicationEventPublisher.publishEvent(
                new ComponentRootInitializedEvent(this, InitializedComponent.root(getComponentEnum(), BigDecimal.ZERO, true))
        );
    }

    @SneakyThrows
    @Override
    public boolean isReady(InvokeVO invokeVO) {
        return super.isReady(invokeVO);
    }

    @Override
    protected void atMethodInvokeBefore(InvokeVO invokeVO, MethodInvokeVO methodInvokeVO) {
        super.atMethodInvokeBefore(invokeVO, methodInvokeVO);
    }

    @Override
    protected void atMethodInvokeAfter(InvokeVO invokeVO, MethodInvokeVO methodInvokeVO) {
        super.atMethodInvokeAfter(invokeVO, methodInvokeVO);

        //记录耗时
        BeanLifeCycleDuration beanLifeCycleDuration = BeanLifeCycleDuration.create(invokeVO.getClazz().getName() + "#afterPropertiesSet", methodInvokeVO);
        applicationEventPublisher.publishEvent(
                new BeanInitMethodInvokeLifeCycleEvent(this, invokeVO.getClazz().getName(), beanLifeCycleDuration, 1)
        );
    }

    @Override
    protected Object[] getParams(InvokeVO invokeVO) {
        return new String[]{invokeVO.getTarget().getClass().getName()};
    }

    @Override
    protected String childName(InvokeVO invokeVO) {
        return invokeVO.getTarget().getClass().getName();
    }

    public static class InitializingBeanSpyInterceptorApi implements SpyInterceptorApi {

        @SneakyThrows
        @AtEnter(inline = true)
        public static void atEnter(
                @Binding.This Object target, @Binding.Class Class<?> clazz, @Binding.MethodName String methodName, @Binding.MethodDesc String methodDesc, @Binding.Args Object[] args
        ) {

            Class<?> initializingBeanClass = clazz.getClassLoader().loadClass("org.springframework.beans.factory.InitializingBean");
            if (initializingBeanClass.isAssignableFrom(clazz)) {
                SpyAPI.atEnter(clazz, methodName, methodDesc, target, args, Collections.emptyMap());
            }

        }

        @SneakyThrows
        @AtExit(inline = true)
        public static void atExit(
                @Binding.This Object target, @Binding.Class Class<?> clazz, @Binding.MethodName String methodName, @Binding.MethodDesc String methodDesc, @Binding.Args Object[] args
                , @Binding.Return Object returnObj
        ) {

            Class<?> initializingBeanClass = clazz.getClassLoader().loadClass("org.springframework.beans.factory.InitializingBean");
            if (initializingBeanClass.isAssignableFrom(clazz)) {
                SpyAPI.atExit(clazz, methodName, methodDesc, target, args, returnObj, Collections.emptyMap());
            }

        }

        @AtExceptionExit(inline = true)
        public static void atExceptionExit(
                @Binding.This Object target, @Binding.Class Class<?> clazz, @Binding.MethodName String methodName, @Binding.MethodDesc String methodDesc, @Binding.Args Object[] args
                , @Binding.Throwable Throwable throwable
        ) {
            SpyAPI.atExceptionExit(clazz, methodName, methodDesc, target, args, throwable, Collections.emptyMap());
        }

    }

}
