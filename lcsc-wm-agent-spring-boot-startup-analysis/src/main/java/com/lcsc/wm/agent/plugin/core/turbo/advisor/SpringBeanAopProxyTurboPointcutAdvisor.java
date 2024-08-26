package com.lcsc.wm.agent.plugin.core.turbo.advisor;

import com.alibaba.bytekit.asm.binding.Binding;
import com.alibaba.bytekit.asm.interceptor.annotation.AtExit;
import com.lcsc.wm.agent.core.advisor.SimpleMethodInvokePointcutAdvisor;
import com.lcsc.wm.agent.core.lifecycle.AgentLifeCycleHook;
import com.lcsc.wm.agent.framework.interceptor.SpyInterceptorApi;
import com.lcsc.wm.agent.framework.vo.ClassMethodInfo;
import com.lcsc.wm.agent.framework.vo.InvokeVO;
import com.lcsc.wm.agent.plugin.core.events.SpringBootApplicationLabelEvent;
import com.lcsc.wm.agent.plugin.core.events.SpringBootApplicationScanClassesEvent;
import com.lcsc.wm.agent.plugin.core.vo.SpringBootApplicationInfo;
import com.lcsc.wm.agent.plugin.core.vo.SpringLabelEnum;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;

import java.agent.SpyAPI;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class SpringBeanAopProxyTurboPointcutAdvisor extends SimpleMethodInvokePointcutAdvisor implements AgentLifeCycleHook, ApplicationListener<SpringBootApplicationScanClassesEvent> {

    private final SpringBootApplicationInfo springBootApplicationInfo;

    private Object classLoaderRepository;

    private volatile boolean supportAop = true;

    public SpringBeanAopProxyTurboPointcutAdvisor(ClassMethodInfo classMethodInfo, Class<? extends SpyInterceptorApi> interceptor
            , SpringBootApplicationInfo springBootApplicationInfo) {
        super(classMethodInfo, interceptor);
        this.springBootApplicationInfo = springBootApplicationInfo;
    }

    @Override
    public boolean isReady(InvokeVO invokeVO) {
        return super.isReady(invokeVO) && supportAop;
    }

    @Override
    public void start() {

        //AOP版本
        try {
            Class<?> bcelRepositoryClass = getAopRepositoryClassName(Thread.currentThread().getContextClassLoader());

            applicationEventPublisher.publishEvent(
                    SpringBootApplicationLabelEvent.create(this, SpringLabelEnum.AOP_REPOSITORY, bcelRepositoryClass.getName()));

            Class<?> bcelWeakClassLoaderReferenceClass = Class.forName("org.aspectj.weaver.bcel.BcelWeakClassLoaderReference", true, Thread.currentThread().getContextClassLoader());
            Object bcelWeakClassLoaderReference = bcelWeakClassLoaderReferenceClass.getConstructor(ClassLoader.class).newInstance(Thread.currentThread().getContextClassLoader());

            Class<?> classLoaderReferenceClass = Class.forName("org.aspectj.apache.bcel.util.ClassLoaderReference", true, Thread.currentThread().getContextClassLoader());

            classLoaderRepository = Class.forName("org.aspectj.apache.bcel.util.ClassLoaderRepository", true, Thread.currentThread().getContextClassLoader())
                    .getConstructor(classLoaderReferenceClass).newInstance(bcelWeakClassLoaderReference);


            //未使用AOP依赖
        } catch (Exception e) {
            supportAop = false;
        }

    }

    /**
     * @param event the event to respond to
     * @see org.aspectj.apache.bcel.util.ClassLoaderRepository
     * @see org.aspectj.apache.bcel.util.NonCachingClassLoaderRepository
     */
    @Override
    @SneakyThrows
    public void onApplicationEvent(SpringBootApplicationScanClassesEvent event) {

        for (String className : event.getClasses()) {
            CompletableFuture.runAsync(() -> {
                try {
                    classLoaderRepository.getClass().getMethod("loadClass", String.class).invoke(classLoaderRepository, className);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    log.error("SpringBeanAopProxyTurboPointcutAdvisor#loadClassError", e);
                }
            });
        }

    }

    /**
     * @param classLoader
     * @return
     * @throws NoSuchMethodException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchFieldException
     */
    private Class<?> getAopRepositoryClassName(ClassLoader classLoader) throws NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        //
        Class<?> java15AnnotationFinderClass = classLoader.loadClass("org.aspectj.weaver.reflect.Java15AnnotationFinder");
        Object java15AnnotationFinder = java15AnnotationFinderClass.newInstance();
        //
        java15AnnotationFinderClass.getMethod("setClassLoader", ClassLoader.class).invoke(java15AnnotationFinder, classLoader);
        //
        Field bcelRepositoryField = java15AnnotationFinderClass.getDeclaredField("bcelRepository");
        bcelRepositoryField.setAccessible(true);
        Object bcelRepository = bcelRepositoryField.get(java15AnnotationFinder);
        //
        return bcelRepository.getClass();
    }

    public static class SpringBeanAopProxyTurboSpyInterceptorApi implements SpyInterceptorApi {

        @SneakyThrows
        @AtExit(inline = true)
        public static void atExit(
                @Binding.This Object target, @Binding.Class Class<?> clazz, @Binding.MethodName String methodName, @Binding.MethodDesc String methodDesc, @Binding.Args Object[] args
                , @Binding.Return Object returnObj, @Binding.Field(name = "bcelRepository") Object bcelRepository, @Binding.Field(name = "classLoaderRef") Object classLoaderRef
        ) {
            if ("org.aspectj.apache.bcel.util.NonCachingClassLoaderRepository".equals(bcelRepository.getClass().getName())) {

                Class<?> classLoaderRepositoryClass = Class.forName("org.aspectj.apache.bcel.util.ClassLoaderRepository", true, clazz.getClassLoader());

                Class<?> classLoaderReferenceClass = Class.forName("org.aspectj.apache.bcel.util.ClassLoaderReference", true, clazz.getClassLoader());
                Object classLoaderReference = classLoaderRepositoryClass.getConstructor(classLoaderReferenceClass).newInstance(classLoaderRef);

                Field bcelRepositoryField = target.getClass().getDeclaredField("bcelRepository");
                bcelRepositoryField.setAccessible(true);
                bcelRepositoryField.set(target, classLoaderReference);

                SpyAPI.atExit(clazz, methodName, methodDesc, target, args, returnObj, null);
            }
        }

    }

}
