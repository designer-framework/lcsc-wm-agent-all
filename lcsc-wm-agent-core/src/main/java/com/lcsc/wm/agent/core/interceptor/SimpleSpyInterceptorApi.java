package com.lcsc.wm.agent.core.interceptor;

import com.alibaba.bytekit.asm.binding.Binding;
import com.alibaba.bytekit.asm.interceptor.annotation.AtEnter;
import com.alibaba.bytekit.asm.interceptor.annotation.AtExceptionExit;
import com.alibaba.bytekit.asm.interceptor.annotation.AtExit;
import com.lcsc.wm.agent.framework.interceptor.SpyInterceptorApi;

import java.agent.SpyAPI;

/**
 * 1. 当前类是默认的字节码插桩逻辑, 如果希望监听方法内部的调用, 可参考:
 * {@link  com.lcsc.wm.agent.plugin.core.analysis.bean.InitializingSingletonsPointcutAdvisor.AfterSingletonsInstantiatedSpyInterceptorApi }
 * <p>
 * 2. 如需实现更加复杂的插桩逻辑请， 先熟悉bytekit中各个注解的使用方法, 参考: <a href="https://github.com/alibaba/bytekit">bytekit官方文档</a>
 */
public class SimpleSpyInterceptorApi implements SpyInterceptorApi {

    /**
     * 方法进入之前
     *
     * @param target
     * @param clazz
     * @param methodName
     * @param methodDesc
     * @param args
     */
    @AtEnter(inline = true)
    public static void atEnter(
            @Binding.This Object target, @Binding.Class Class<?> clazz, @Binding.MethodName String methodName, @Binding.MethodDesc String methodDesc, @Binding.Args Object[] args
    ) {
        SpyAPI.atEnter(clazz, methodName, methodDesc, target, args, null);
    }

    /**
     * 方法调用完成之后
     *
     * @param target
     * @param clazz
     * @param methodName
     * @param methodDesc
     * @param args
     * @param returnObj
     */
    @AtExit(inline = true)
    public static void atExit(@Binding.This Object target, @Binding.Class Class<?> clazz, @Binding.MethodName String methodName, @Binding.MethodDesc String methodDesc, @Binding.Args Object[] args, @Binding.Return Object returnObj) {
        SpyAPI.atExit(clazz, methodName, methodDesc, target, args, returnObj, null);
    }

    /**
     * 方法调用时抛出异常
     *
     * @param target
     * @param clazz
     * @param methodName
     * @param methodDesc
     * @param args
     * @param throwable
     */
    @AtExceptionExit(inline = true)
    public static void atExceptionExit(@Binding.This Object target, @Binding.Class Class<?> clazz, @Binding.MethodName String methodName, @Binding.MethodDesc String methodDesc, @Binding.Args Object[] args, @Binding.Throwable Throwable throwable) {
        SpyAPI.atExceptionExit(clazz, methodName, methodDesc, target, args, throwable, null);
    }

}
