package com.lcsc.wm.agent.framework.advisor;

import com.lcsc.wm.agent.framework.advice.Advice;
import com.lcsc.wm.agent.framework.interceptor.AbstractMethodInvokeInterceptor;
import com.lcsc.wm.agent.framework.pointcut.Pointcut;
import com.lcsc.wm.agent.framework.source.AgentSourceAttribute;
import com.lcsc.wm.agent.framework.state.AgentState;
import com.lcsc.wm.agent.framework.interceptor.SpyInterceptorApi;
import com.lcsc.wm.agent.framework.pointcut.CachingPointcut;
import com.lcsc.wm.agent.framework.vo.ClassMethodInfo;
import com.lcsc.wm.agent.framework.vo.InvokeVO;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.util.Assert;

/**
 * 参见
 * {@link com.lcsc.wm.agent.core.command.monitor200.StackAdviceListener}
 */
@Slf4j
public abstract class AbstractMethodInvokePointcutAdvisor extends AbstractMethodInvokeInterceptor implements PointcutAdvisor, ApplicationContextAware, ApplicationEventPublisherAware, InitializingBean {

    @Setter
    protected ApplicationEventPublisher applicationEventPublisher;

    @Setter
    protected ApplicationContext applicationContext;

    @Getter
    private AgentState agentState;

    @Setter
    private Pointcut pointcut = Pointcut.FALSE;

    @Getter
    @Setter
    private AgentSourceAttribute agentSourceAttribute;

    public AbstractMethodInvokePointcutAdvisor() {
    }

    /**
     * @param classMethodInfo
     * @param canRetransform
     * @param interceptor     默认值是不会生成代理类的
     */
    public AbstractMethodInvokePointcutAdvisor(ClassMethodInfo classMethodInfo, Boolean canRetransform, Class<? extends SpyInterceptorApi> interceptor) {
        agentSourceAttribute = new AgentSourceAttribute(classMethodInfo);
        pointcut = new CachingPointcut(agentSourceAttribute, canRetransform, interceptor);
    }

    @Override
    public boolean isReady(InvokeVO invokeVO) {
        return getAgentState().isStarted();
    }

    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }

    @Override
    public Advice getAdvice() {
        return this;
    }

    protected ClassMethodInfo getClassMethodInfo() {
        return agentSourceAttribute.getSourceAttribute();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.agentState = applicationContext.getBean(AgentState.class);
        Assert.notNull(agentState, "AgentState");
        if (pointcut == Pointcut.FALSE) {
            log.error("默认的Pointcut,  请检查配置是否正确: {}", getClass());
        }
    }

}
