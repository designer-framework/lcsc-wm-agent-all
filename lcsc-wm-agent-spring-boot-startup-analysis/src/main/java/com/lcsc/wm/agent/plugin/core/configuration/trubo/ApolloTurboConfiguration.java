package com.lcsc.wm.agent.plugin.core.configuration.trubo;

import com.lcsc.wm.agent.core.annotation.EnabledInstrument;
import com.lcsc.wm.agent.core.annotation.Retransform;
import com.lcsc.wm.agent.framework.vo.ClassMethodInfo;
import com.lcsc.wm.agent.plugin.core.analysis.component.ApolloCreatorPointcutAdvisor;
import com.lcsc.wm.agent.plugin.core.annotation.ConditionalOnTurboPropCondition;
import com.lcsc.wm.agent.plugin.core.enums.SpringComponentEnum;
import com.lcsc.wm.agent.plugin.core.turbo.advisor.ApolloCreatorTurboPointcutAdvisor;
import com.lcsc.wm.agent.plugin.core.turbo.instrument.ApolloInjector;
import com.lcsc.wm.agent.plugin.core.turbo.instrument.DefaultConfigManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @see com.ctrip.framework.apollo.build.ApolloInjector
 */
//替换类中同方法名同入参的方法
@EnabledInstrument({
        //instrumentClass中同名同入参的方法 覆盖 用户类className中的方法, 方法名和入参相同的方法才会替换)
        @Retransform(ApolloInjector.class),
        @Retransform(DefaultConfigManager.class)
})
@Configuration(proxyBeanMethods = false)
//激活配置, 该Configuration类才生效
@ConditionalOnTurboPropCondition(pluginName = "apollo")
public class ApolloTurboConfiguration {

    /**
     * 将Apollo命名空间的加载由串行改成并行, 还需要解决命名空间重复加载 及 并行并行加载配置导致占位符解析失败的问题, 所以必须等所有配置并行加载完成后才能执行后续代码块
     *
     * @return
     * @see com.ctrip.framework.apollo.ConfigService#getConfig(java.lang.String)
     */
    @Bean
    @ConditionalOnMissingBean(ApolloCreatorPointcutAdvisor.class)
    public ApolloCreatorTurboPointcutAdvisor apolloCreatorTurboPointcutAdvisor() {
        return new ApolloCreatorTurboPointcutAdvisor(
                SpringComponentEnum.APOLLO_APPLICATION_CONTEXT_INITIALIZER
                , ClassMethodInfo.create("com.ctrip.framework.apollo.spring.boot.ApolloApplicationContextInitializer#initialize(org.springframework.core.env.ConfigurableEnvironment)")
        );

    }

}
