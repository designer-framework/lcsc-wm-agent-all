package com.lcsc.wm.agent.plugin.extension.configuration;

import com.lcsc.wm.agent.core.advisor.SimpleMethodInvokePointcutAdvisor;
import com.lcsc.wm.agent.core.annotation.EnabledMethodInvokeWatch;
import com.lcsc.wm.agent.core.annotation.MethodInvokeWatch;
import com.lcsc.wm.agent.framework.interceptor.SpyInterceptorApi;
import com.lcsc.wm.agent.framework.vo.ClassMethodInfo;
import com.lcsc.wm.agent.framework.vo.InvokeVO;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-26 23:04
 */
@EnabledMethodInvokeWatch({
        //spring-boot3.2.0+= 类加载器
        @MethodInvokeWatch(value = "org.springframework.boot.loader.launch.LaunchedClassloader#loadClass(java.lang.String, boolean)", canRetransform = true, pointcutAdvisor = SpringClassLoaderMethodInvokeAutoConfiguration.IgnoreExceptionMethodInvokePointcutAdvisor.class),
        //spring-boot3.2.0- 类加载器
        @MethodInvokeWatch(value = "org.springframework.boot.loader.LaunchedURLClassLoader#loadClass(java.lang.String, boolean)", canRetransform = true, pointcutAdvisor = SpringClassLoaderMethodInvokeAutoConfiguration.IgnoreExceptionMethodInvokePointcutAdvisor.class),
})
@Configuration(proxyBeanMethods = false)
public class SpringClassLoaderMethodInvokeAutoConfiguration {

    static class IgnoreExceptionMethodInvokePointcutAdvisor extends SimpleMethodInvokePointcutAdvisor {

        public IgnoreExceptionMethodInvokePointcutAdvisor(ClassMethodInfo classMethodInfo, Boolean canRetransform, Class<? extends SpyInterceptorApi> spyInterceptorClass) {
            super(classMethodInfo, canRetransform, spyInterceptorClass);
        }

        @Override
        public void atAfterThrowing(InvokeVO invokeVO) {
            this.atExit(invokeVO);
        }

    }

}
