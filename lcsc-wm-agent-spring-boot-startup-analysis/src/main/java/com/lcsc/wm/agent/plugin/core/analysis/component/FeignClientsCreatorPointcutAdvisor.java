package com.lcsc.wm.agent.plugin.core.analysis.component;

import com.alibaba.bytekit.asm.binding.Binding;
import com.alibaba.bytekit.asm.interceptor.annotation.AtEnter;
import com.alibaba.bytekit.asm.interceptor.annotation.AtExceptionExit;
import com.alibaba.bytekit.asm.interceptor.annotation.AtExit;
import com.lcsc.wm.agent.framework.interceptor.SpyInterceptorApi;
import com.lcsc.wm.agent.framework.vo.ClassMethodInfo;
import com.lcsc.wm.agent.framework.vo.InvokeVO;
import com.lcsc.wm.agent.plugin.core.enums.SpringComponentEnum;
import com.lcsc.wm.agent.plugin.core.events.ComponentRootInitializedEvent;
import com.lcsc.wm.agent.plugin.core.vo.InitializedComponent;
import lombok.extern.slf4j.Slf4j;

import java.agent.SpyAPI;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Spring项目启动耗时分析
 */
@Slf4j
public class FeignClientsCreatorPointcutAdvisor extends AbstractComponentChildCreatorPointcutAdvisor {

    private static final String TYPE = "type";

    public FeignClientsCreatorPointcutAdvisor(
            SpringComponentEnum springComponentEnum, ClassMethodInfo classMethodInfo, Class<? extends SpyInterceptorApi> interceptor
    ) {
        super(springComponentEnum, classMethodInfo, interceptor);
    }

    @Override
    public void start() {
        super.start();
        applicationEventPublisher.publishEvent(new ComponentRootInitializedEvent(
                this, InitializedComponent.root(SpringComponentEnum.FEIGN_CLIENT_FACTORY_BEAN, BigDecimal.ZERO, true)
        ));
    }

    @Override
    protected Object[] getParams(InvokeVO invokeVO) {
        Map<String, Object> attach = invokeVO.getAttach();
        return new Object[]{((Class<?>) attach.get(TYPE)).getName()};
    }

    @Override
    protected String childName(InvokeVO invokeVO) {
        return ((Class<?>) invokeVO.getAttach().get(TYPE)).getName();
    }

    public static class FeignClientSpyInterceptorApi implements SpyInterceptorApi {

        @AtEnter(inline = true)
        public static void atEnter(
                @Binding.This Object target, @Binding.Class Class<?> clazz, @Binding.MethodName String methodName, @Binding.MethodDesc String methodDesc, @Binding.Args Object[] args
                , @Binding.Field(name = "type") Object type
        ) {
            Map<String, Object> attach = new HashMap<>();
            attach.put(TYPE, type);
            SpyAPI.atEnter(clazz, methodName, methodDesc, target, args, attach);
        }

        @AtExit(inline = true)
        public static void atExit(
                @Binding.This Object target, @Binding.Class Class<?> clazz, @Binding.MethodName String methodName, @Binding.MethodDesc String methodDesc, @Binding.Args Object[] args
                , @Binding.Return Object returnObj, @Binding.Field(name = "type") Object type
        ) {
            Map<String, Object> attach = new HashMap<>();
            attach.put("type", type);
            SpyAPI.atExit(clazz, methodName, methodDesc, target, args, returnObj, attach);
        }

        @AtExceptionExit(inline = true)
        public static void atExceptionExit(
                @Binding.This Object target, @Binding.Class Class<?> clazz, @Binding.MethodName String methodName, @Binding.MethodDesc String methodDesc, @Binding.Args Object[] args
                , @Binding.Throwable Throwable throwable, @Binding.Field(name = "type") Object type
        ) {
            Map<String, Object> attach = new HashMap<>();
            attach.put("type", type);
            SpyAPI.atExceptionExit(clazz, methodName, methodDesc, target, args, throwable, attach);
        }

    }

}
