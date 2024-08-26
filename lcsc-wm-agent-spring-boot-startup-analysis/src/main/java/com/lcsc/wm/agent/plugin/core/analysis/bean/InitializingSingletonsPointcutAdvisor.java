package com.lcsc.wm.agent.plugin.core.analysis.bean;

import com.alibaba.bytekit.asm.binding.Binding;
import com.alibaba.bytekit.asm.interceptor.annotation.AtExceptionExit;
import com.alibaba.bytekit.asm.interceptor.annotation.AtInvoke;
import com.lcsc.wm.agent.core.lifecycle.AgentLifeCycleHook;
import com.lcsc.wm.agent.core.vo.MethodInvokeVO;
import com.lcsc.wm.agent.framework.interceptor.SpyInterceptorApi;
import com.lcsc.wm.agent.framework.vo.ClassMethodInfo;
import com.lcsc.wm.agent.framework.vo.InvokeVO;
import com.lcsc.wm.agent.plugin.core.analysis.component.AbstractComponentChildCreatorPointcutAdvisor;
import com.lcsc.wm.agent.plugin.core.enums.ComponentEnum;
import com.lcsc.wm.agent.plugin.core.enums.SpringComponentEnum;
import com.lcsc.wm.agent.plugin.core.events.ComponentRootInitializedEvent;
import com.lcsc.wm.agent.plugin.core.events.SmartInstantiateSingletonLifeCycleEvent;
import com.lcsc.wm.agent.plugin.core.vo.BeanLifeCycleDuration;
import com.lcsc.wm.agent.plugin.core.vo.InitializedComponent;

import java.agent.SpyAPI;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InitializingSingletonsPointcutAdvisor extends AbstractComponentChildCreatorPointcutAdvisor implements AgentLifeCycleHook {

    private static final String beanName = "beanName";

    public InitializingSingletonsPointcutAdvisor(ComponentEnum componentEnum, ClassMethodInfo classMethodInfo, Class<? extends SpyInterceptorApi> interceptor) {
        super(componentEnum, classMethodInfo, interceptor);
    }

    @Override
    public void start() {
        applicationEventPublisher.publishEvent(new ComponentRootInitializedEvent(
                this, InitializedComponent.root(SpringComponentEnum.SMART_INITIALIZING_SINGLETON, BigDecimal.ZERO, true)
        ));
    }

    /**
     * @param invokeVO
     * @return
     * @see org.springframework.beans.factory.support.DefaultListableBeanFactory#preInstantiateSingletons()
     * @see InitializingSingletonsPointcutAdvisor.AfterSingletonsInstantiatedSpyInterceptorApi#atEnter(java.lang.Object, java.lang.Class, java.lang.String, java.lang.String, java.lang.Object[], java.lang.Object[], java.lang.String[])
     */
    @Override
    protected Object[] getParams(InvokeVO invokeVO) {
        return Optional.of(invokeVO.getAttach())
                .map(singletonInstance -> singletonInstance.get("singletonInstance"))
                .map(object -> new Object[]{object.getClass().getName()})
                .orElseGet(() -> new Object[0]);
    }

    @Override
    protected String childName(InvokeVO invokeVO) {
        return String.valueOf(invokeVO.getAttach().get(beanName));
    }

    @Override
    protected void atMethodInvokeAfter(InvokeVO invokeVO, MethodInvokeVO methodInvokeVO) {
        super.atMethodInvokeAfter(invokeVO, methodInvokeVO);

        String beanName = childName(invokeVO);
        //统计耗时
        BeanLifeCycleDuration beanLifeCycleDuration = BeanLifeCycleDuration.create(beanName, methodInvokeVO);
        applicationEventPublisher.publishEvent(
                new SmartInstantiateSingletonLifeCycleEvent(this, beanName, beanLifeCycleDuration)
        );
    }

    public static class AfterSingletonsInstantiatedSpyInterceptorApi implements SpyInterceptorApi {

        @AtInvoke(whenComplete = false, inline = true, name = "afterSingletonsInstantiated")
        public static void atEnter(
                @Binding.This Object target, @Binding.Class Class<?> clazz, @Binding.MethodName String methodName, @Binding.MethodDesc String methodDesc, @Binding.Args Object[] args
                , @Binding.LocalVars Object[] vars, @Binding.LocalVarNames String[] varNames
        ) {
            if (varNames != null && varNames.length > 0) {
                Map<String, Object> attach = new HashMap<>();
                for (int i = 0; i < varNames.length; i++) {
                    attach.put(varNames[i], vars[i]);
                }
                SpyAPI.atEnter(clazz, methodName, methodDesc, target, args, attach);
            } else {
                SpyAPI.atEnter(clazz, methodName, methodDesc, target, args, Collections.emptyMap());
            }
        }

        @AtInvoke(whenComplete = true, inline = true, name = "afterSingletonsInstantiated")
        public static void atExit
                (@Binding.This Object target, @Binding.Class Class<?> clazz, @Binding.MethodName String methodName, @Binding.MethodDesc String methodDesc, @Binding.Args Object[] args
                        , @Binding.LocalVars Object[] vars, @Binding.LocalVarNames String[] varNames
                ) {
            if (varNames != null && varNames.length > 0) {
                Map<String, Object> attach = new HashMap<>();
                for (int i = 0; i < varNames.length; i++) {
                    attach.put(varNames[i], vars[i]);
                }
                SpyAPI.atExit(clazz, methodName, methodDesc, target, args, null, attach);
            } else {
                SpyAPI.atExit(clazz, methodName, methodDesc, target, args, null, Collections.emptyMap());
            }
        }

        @AtExceptionExit(inline = true)
        public static void atExceptionExit(@Binding.This Object target, @Binding.Class Class<?> clazz, @Binding.MethodName String methodName, @Binding.MethodDesc String methodDesc, @Binding.Args Object[] args, @Binding.Throwable Throwable throwable) {
            SpyAPI.atExceptionExit(clazz, methodName, methodDesc, target, args, throwable, null);
        }

    }

}
