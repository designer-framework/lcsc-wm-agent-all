package com.lcsc.wm.agent.framework.pointcut;

import com.lcsc.wm.agent.framework.source.AgentSourceAttribute;
import com.lcsc.wm.agent.framework.interceptor.SpyInterceptorApi;
import lombok.Data;
import org.springframework.util.Assert;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-23 22:30
 * 在类加载阶段被解析, 命中后就会被缓存起来, 后续触发方法插桩埋点时, 直接从缓存中读取, 插桩点越多性能优势越明显
 * @see com.lcsc.wm.agent.core.instrument.EnhanceProfilingInstrumentTransformer#transform(java.lang.ClassLoader, java.lang.String, java.lang.Class, java.security.ProtectionDomain, byte[])
 */
@Data
public class CachingPointcut implements Pointcut {

    private final boolean canRetransform;

    private final AgentSourceAttribute agentSourceAttribute;

    private Class<? extends SpyInterceptorApi> interceptor;

    public CachingPointcut(AgentSourceAttribute agentSourceAttribute, Boolean canRetransform, Class<? extends SpyInterceptorApi> interceptor) {
        this.agentSourceAttribute = agentSourceAttribute;
        this.canRetransform = canRetransform;
        this.interceptor = interceptor;
        Assert.notNull(interceptor, "SpyInterceptorClass");
    }

    @Override
    public boolean getCanRetransform() {
        return canRetransform;
    }

    @Override
    public boolean isCandidateClass(String className) {
        return agentSourceAttribute.isCandidateClass(className);
    }

    @Override
    public boolean isCandidateMethod(String className, String methodName, String methodDesc) {
        return this.agentSourceAttribute.isCandidateMethod(className, methodName, methodDesc);
    }

    /**
     * 是否被缓存过(是否为符合条件的插桩方法)
     *
     * @param className
     * @param methodName
     * @param methodDesc
     * @return
     */
    @Override
    public boolean isHit(String className, String methodName, String methodDesc) {
        return this.agentSourceAttribute.getSourceAttribute(className, methodName, methodDesc) != null;
    }

}
