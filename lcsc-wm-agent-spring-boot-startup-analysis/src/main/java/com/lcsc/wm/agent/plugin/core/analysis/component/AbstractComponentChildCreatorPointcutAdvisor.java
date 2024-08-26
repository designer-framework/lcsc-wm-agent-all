package com.lcsc.wm.agent.plugin.core.analysis.component;

import com.lcsc.wm.agent.core.advisor.SimpleMethodInvokePointcutAdvisor;
import com.lcsc.wm.agent.core.lifecycle.AgentLifeCycleHook;
import com.lcsc.wm.agent.core.vo.MethodInvokeVO;
import com.lcsc.wm.agent.framework.interceptor.SpyInterceptorApi;
import com.lcsc.wm.agent.framework.vo.ClassMethodInfo;
import com.lcsc.wm.agent.framework.vo.InvokeVO;
import com.lcsc.wm.agent.plugin.core.enums.ComponentEnum;
import com.lcsc.wm.agent.plugin.core.events.ComponentChildInitializedEvent;
import com.lcsc.wm.agent.plugin.core.vo.InitializedComponent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;

/**
 * Spring项目启动耗时分析
 */
@Slf4j
public abstract class AbstractComponentChildCreatorPointcutAdvisor extends SimpleMethodInvokePointcutAdvisor implements AgentLifeCycleHook {

    private final ThreadLocal<InitializedComponent> children = new ThreadLocal<>();

    @Getter
    private final ComponentEnum componentEnum;

    public AbstractComponentChildCreatorPointcutAdvisor(
            ComponentEnum componentEnum, ClassMethodInfo classMethodInfo
    ) {
        super(classMethodInfo);
        this.componentEnum = componentEnum;
    }

    public AbstractComponentChildCreatorPointcutAdvisor(ComponentEnum componentEnum, ClassMethodInfo classMethodInfo, Class<? extends SpyInterceptorApi> interceptor) {
        super(classMethodInfo, interceptor);
        this.componentEnum = componentEnum;
    }

    /**
     * 新增一个Child
     *
     * @param invokeVO
     * @param methodInvokeVO
     */
    @Override
    protected void atMethodInvokeBefore(InvokeVO invokeVO, MethodInvokeVO methodInvokeVO) {
        children.set(newComponentNode(invokeVO, methodInvokeVO));
    }

    /**
     * 组件子条目, 如果没有子条目则不会进该方法
     *
     * @param invokeVO
     * @return
     */
    protected InitializedComponent newComponentNode(InvokeVO invokeVO, MethodInvokeVO methodInvokeVO) {
        InitializedComponent child = InitializedComponent.child(getComponentEnum(), childName(invokeVO), methodInvokeVO.getStartMillis());
        //child.setEndMillis(BigDecimal.ZERO);
        child.setDuration(methodInvokeVO.getDuration());
        return child;
    }

    protected abstract String childName(InvokeVO invokeVO);

    @Override
    protected void atMethodInvokeAfter(InvokeVO invokeVO, MethodInvokeVO methodInvokeVO) {
        //继承方法调用的耗时
        InitializedComponent initializedComponent = getInitializedComponent();
        initializedComponent.setEndMillis(methodInvokeVO.getEndMillis());
        initializedComponent.setDuration(methodInvokeVO.getDuration());

        applicationEventPublisher.publishEvent(
                new ComponentChildInitializedEvent(this, Collections.singletonList(initializedComponent))
        );
    }

    protected InitializedComponent getInitializedComponent() {
        return children.get();
    }

    @Override
    public void destroy() {
        super.destroy();
        children.remove();
    }

}
