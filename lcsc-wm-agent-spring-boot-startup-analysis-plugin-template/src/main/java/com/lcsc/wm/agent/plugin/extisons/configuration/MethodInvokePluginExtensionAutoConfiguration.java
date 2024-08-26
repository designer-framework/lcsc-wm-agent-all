package com.lcsc.wm.agent.plugin.extisons.configuration;

import com.lcsc.wm.agent.core.advisor.SimpleMethodInvokePointcutAdvisor;
import com.lcsc.wm.agent.core.annotation.EnabledMethodInvokeWatch;
import com.lcsc.wm.agent.core.annotation.MethodInvokeWatch;
import com.lcsc.wm.agent.framework.vo.ClassMethodInfo;
import com.lcsc.wm.agent.plugin.extisons.interceptor.PrintMethodInvokeSpyInterceptorApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 通过注解实现对字节码的插桩(方法调用耗时统计插桩)
 */
@Configuration
@EnabledMethodInvokeWatch({
        //SpringApplicationBannerPrinter#print方法被调用时, 输出日志
        @MethodInvokeWatch(
                value = "org.springframework.boot.SpringApplicationBannerPrinter#print(org.springframework.core.env.Environment, java.lang.Class, java.io.PrintStream)",
                interceptor = PrintMethodInvokeSpyInterceptorApi.class
        )
})
public class MethodInvokePluginExtensionAutoConfiguration {

    @Bean
    public SimpleMethodInvokePointcutAdvisor demoPointcutAdvisor() {
        return new SimpleMethodInvokePointcutAdvisor(
                ClassMethodInfo.create("fullyQualifiedClassName#methodName()")
        );
    }

}
