package com.lcsc.wm.agent.plugin.extension.interceptor;

import com.alibaba.bytekit.asm.binding.Binding;
import com.alibaba.bytekit.asm.interceptor.annotation.AtEnter;
import com.alibaba.bytekit.asm.interceptor.annotation.AtExceptionExit;
import com.alibaba.bytekit.asm.interceptor.annotation.AtExit;
import com.lcsc.wm.agent.framework.interceptor.SpyInterceptorApi;

public class PrintMethodInvokeSpyInterceptorApi implements SpyInterceptorApi {

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
    public static void atEnter(@Binding.This Object target, @Binding.Class Class<?> clazz, @Binding.MethodName String methodName, @Binding.MethodDesc String methodDesc, @Binding.Args Object[] args) {
        System.out.println(String.format("方法调用开始： %s#%s", clazz.getName(), methodName));
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
        System.out.println(String.format("方法调用完成： %s#%s", clazz.getName(), methodName));
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
        System.out.println(String.format("方法调用异常： %s#%s", clazz.getName(), methodName));
    }

}
