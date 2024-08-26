package com.lcsc.wm.agent.plugin.core.configuration.trubo;


import com.lcsc.wm.agent.core.annotation.EnabledInstrument;
import com.lcsc.wm.agent.core.annotation.Retransform;
import com.lcsc.wm.agent.framework.vo.ClassMethodInfo;
import com.lcsc.wm.agent.plugin.core.annotation.ConditionalOnTurboPropCondition;
import com.lcsc.wm.agent.plugin.core.turbo.advisor.SpringBeanAopProxyTurboPointcutAdvisor;
import com.lcsc.wm.agent.plugin.core.turbo.instrument.aop.TurboClassLoaderRepository;
import com.lcsc.wm.agent.plugin.core.vo.SpringBootApplicationInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 替换类中同方法名同入参的方法
 * instrumentClass中同名同入参的方法 覆盖 用户类className中的方法, 方法名和入参相同的方法才会替换)
 */
@EnabledInstrument({
        //低版本AOP
        @Retransform(TurboClassLoaderRepository.class),
        //新版本AOP
        @Retransform(TurboClassLoaderRepository.class)
})
@Configuration(proxyBeanMethods = false)
@ConditionalOnTurboPropCondition(pluginName = "aop")
public class SpringAopTurboConfiguration {

    @Bean
    SpringBeanAopProxyTurboPointcutAdvisor springBeanAopProxyTurboPointcutAdvisor(SpringBootApplicationInfo springBootApplicationInfo) {
        return new SpringBeanAopProxyTurboPointcutAdvisor(
                ClassMethodInfo.create("org.aspectj.weaver.reflect.Java15AnnotationFinder#setClassLoader(java.lang.ClassLoader)")
                , SpringBeanAopProxyTurboPointcutAdvisor.SpringBeanAopProxyTurboSpyInterceptorApi.class
                , springBootApplicationInfo
        );
    }

}
