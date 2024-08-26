package com.lcsc.wm.agent.core.configuration.advisor;

import com.lcsc.wm.agent.core.interceptor.SimpleSpyInterceptorApi;
import com.lcsc.wm.agent.framework.advisor.AbstractMethodInvokePointcutAdvisor;
import com.lcsc.wm.agent.framework.interceptor.SpyInterceptorApi;
import com.lcsc.wm.agent.framework.pointcut.CachingPointcut;
import com.lcsc.wm.agent.framework.source.AgentSourceAttribute;
import com.lcsc.wm.agent.framework.vo.ClassMethodInfo;

public class AdvisorUtils {

    public static <T extends AbstractMethodInvokePointcutAdvisor> T build(T abstractMethodInvokePointcutAdvisor, String fullyQualifiedMethodName) {
        return build(abstractMethodInvokePointcutAdvisor, fullyQualifiedMethodName, null);
    }

    public static <T extends AbstractMethodInvokePointcutAdvisor> T build(
            T abstractMethodInvokePointcutAdvisor, String fullyQualifiedMethodName
            , Class<? extends SpyInterceptorApi> interceptor
    ) {
        //AgentSourceAttribute
        AgentSourceAttribute agentSourceAttribute = new AgentSourceAttribute(ClassMethodInfo.create(fullyQualifiedMethodName));

        //切点
        abstractMethodInvokePointcutAdvisor.setPointcut(
                new CachingPointcut(agentSourceAttribute, Boolean.FALSE, interceptor == null ? SimpleSpyInterceptorApi.class : interceptor)
        );

        //
        abstractMethodInvokePointcutAdvisor.setAgentSourceAttribute(agentSourceAttribute);

        return abstractMethodInvokePointcutAdvisor;
    }

}
