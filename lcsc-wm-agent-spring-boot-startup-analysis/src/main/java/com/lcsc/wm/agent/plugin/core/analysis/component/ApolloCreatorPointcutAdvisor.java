package com.lcsc.wm.agent.plugin.core.analysis.component;

import com.lcsc.wm.agent.plugin.core.enums.ComponentEnum;
import com.lcsc.wm.agent.framework.vo.ClassMethodInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Apollo加载配置耗时统计
 */
@Slf4j
public class ApolloCreatorPointcutAdvisor extends AbstractComponentRootCreatorPointcutAdvisor {

    /**
     * @return
     * @see com.ctrip.framework.apollo.spring.boot.ApolloApplicationContextInitializer#initialize(ConfigurableEnvironment)
     */
    public ApolloCreatorPointcutAdvisor(ComponentEnum componentEnum, ClassMethodInfo classMethodInfo) {
        super(componentEnum, classMethodInfo);
    }

}
