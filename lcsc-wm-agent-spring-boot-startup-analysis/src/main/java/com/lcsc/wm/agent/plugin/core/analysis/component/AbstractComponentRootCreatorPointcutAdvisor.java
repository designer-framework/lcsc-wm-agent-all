package com.lcsc.wm.agent.plugin.core.analysis.component;

import com.lcsc.wm.agent.core.advisor.SimpleMethodInvokePointcutAdvisor;
import com.lcsc.wm.agent.core.constants.LifeCycleOrdered;
import com.lcsc.wm.agent.core.lifecycle.AgentLifeCycleHook;
import com.lcsc.wm.agent.core.vo.MethodInvokeVO;
import com.lcsc.wm.agent.framework.interceptor.SpyInterceptorApi;
import com.lcsc.wm.agent.framework.vo.ClassMethodInfo;
import com.lcsc.wm.agent.framework.vo.InvokeVO;
import com.lcsc.wm.agent.plugin.core.enums.ComponentEnum;
import com.lcsc.wm.agent.plugin.core.events.ComponentRootInitializedEvent;
import com.lcsc.wm.agent.plugin.core.vo.InitializedComponent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Spring项目启动耗时分析
 */
@Slf4j
public abstract class AbstractComponentRootCreatorPointcutAdvisor extends SimpleMethodInvokePointcutAdvisor implements AgentLifeCycleHook {

    @Getter
    private final ComponentEnum componentEnum;

    private final ThreadLocal<InitializedComponent> component = new ThreadLocal<>();

    private final Lock lock = new ReentrantLock();

    @Getter
    private volatile boolean started;

    public AbstractComponentRootCreatorPointcutAdvisor(
            ComponentEnum componentEnum,
            ClassMethodInfo classMethodInfo
    ) {
        super(classMethodInfo);
        this.componentEnum = componentEnum;
    }

    public AbstractComponentRootCreatorPointcutAdvisor(
            ComponentEnum componentEnum, ClassMethodInfo classMethodInfo
            , Class<? extends SpyInterceptorApi> interceptor
    ) {
        super(classMethodInfo, interceptor);
        this.componentEnum = componentEnum;
    }

    @Override
    protected void atMethodInvokeBefore(InvokeVO invokeVO, MethodInvokeVO methodInvokeVO) {
        lock.lock();
        //首次启动组件
        if (!started) {
            component.set(newComponentNode(invokeVO, methodInvokeVO));
            applicationEventPublisher.publishEvent(new ComponentRootInitializedEvent(this, component.get()));
        }
    }

    /**
     * 组件子条目, 如果没有子条目则不会进该方法
     *
     * @param invokeVO
     * @return
     */
    protected InitializedComponent newComponentNode(InvokeVO invokeVO, MethodInvokeVO methodInvokeVO) {
        return InitializedComponent.root(componentEnum, methodInvokeVO.getStartMillis());
    }

    @Override
    protected void atMethodInvokeAfter(InvokeVO invokeVO, MethodInvokeVO methodInvokeVO) {
        //继承方法调用的耗时
        if (!started) {
            InitializedComponent initializedComponent = getInitializedComponent();
            initializedComponent.setEndMillis(methodInvokeVO.getEndMillis());
            initializedComponent.setDuration(methodInvokeVO.getDuration());
            started = true;
            lock.unlock();
        }
    }

    protected InitializedComponent getInitializedComponent() {
        return component.get();
    }

    @Override
    public void destroy() {
        super.destroy();
        component.remove();
    }

    @Override
    public int getOrder() {
        return LifeCycleOrdered.UPLOAD_STATISTICS;
    }

}
