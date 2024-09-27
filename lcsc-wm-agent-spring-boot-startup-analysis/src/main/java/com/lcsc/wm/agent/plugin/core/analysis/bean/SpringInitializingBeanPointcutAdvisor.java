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
import com.lcsc.wm.agent.plugin.core.events.ComponentRootInitializedEvent;
import com.lcsc.wm.agent.plugin.core.events.InitializingBeanMethodInvokeLifeCycleEvent;
import com.lcsc.wm.agent.plugin.core.vo.BeanLifeCycleDuration;
import com.lcsc.wm.agent.plugin.core.vo.InitializedComponent;
import lombok.SneakyThrows;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.agent.SpyAPI;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @see org.springframework.beans.factory.InitializingBean
 * @see org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#invokeInitMethods(java.lang.String, java.lang.Object, org.springframework.beans.factory.support.RootBeanDefinition)
 * @see
 */
public class SpringInitializingBeanPointcutAdvisor extends AbstractComponentChildCreatorPointcutAdvisor implements DisposableBean, InitializingBean {

    private final CreatingBean creatingBean;

    private final ThreadLocal<Map<Integer, Long>> initialized = ThreadLocal.withInitial(HashMap::new);

    /**
     * @param componentEnum
     * @param classMethodInfo
     * @param interceptor
     * @see org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#invokeInitMethods(java.lang.String, java.lang.Object, org.springframework.beans.factory.support.RootBeanDefinition)
     */
    public SpringInitializingBeanPointcutAdvisor(ComponentEnum componentEnum, ClassMethodInfo classMethodInfo, Class<? extends SpyInterceptorApi> interceptor, CreatingBean creatingBean) {
        super(componentEnum, classMethodInfo, interceptor);
        this.creatingBean = creatingBean;
    }

    @Override
    public void start() {
        super.start();
        applicationEventPublisher.publishEvent(
                new ComponentRootInitializedEvent(this, InitializedComponent.root(getComponentEnum(), BigDecimal.ZERO, true))
        );
    }

    @Override
    public void before(InvokeVO invokeVO) throws Throwable {
        this.initialized.get().computeIfAbsent(System.identityHashCode(invokeVO.getTarget()), key -> invokeVO.getInvokeId());
        super.before(invokeVO);
    }

    @Override
    public boolean isReady(InvokeVO invokeVO) {
        return super.isReady(invokeVO) && creatingBean.getCreatingBeanName() != null && this.initialized.get().get(System.identityHashCode(invokeVO.getTarget())) == invokeVO.getInvokeId();
    }

    @Override
    protected void atBefore(InvokeVO invokeVO) {
        super.atBefore(invokeVO);
    }

    @Override
    protected void atMethodInvokeAfter(InvokeVO invokeVO, MethodInvokeVO methodInvokeVO) {
        super.atMethodInvokeAfter(invokeVO, methodInvokeVO);

        //记录耗时
        BeanLifeCycleDuration beanLifeCycleDuration = BeanLifeCycleDuration.create(invokeVO.getClazz().getName() + "#afterPropertiesSet", methodInvokeVO);
        applicationEventPublisher.publishEvent(
                new InitializingBeanMethodInvokeLifeCycleEvent(this, creatingBean.getCreatingBeanName(), beanLifeCycleDuration)
        );

    }

    @Override
    protected Object[] getParams(InvokeVO invokeVO) {
        return new String[]{creatingBean.getCreatingBeanName()};
    }

    @Override
    protected String childName(InvokeVO invokeVO) {
        return creatingBean.getCreatingBeanName();
    }

    @Override
    public void destroy() {
        super.destroy();
        initialized.remove();
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
