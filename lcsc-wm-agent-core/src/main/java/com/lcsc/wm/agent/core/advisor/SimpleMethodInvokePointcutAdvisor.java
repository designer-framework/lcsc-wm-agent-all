package com.lcsc.wm.agent.core.advisor;

import com.lcsc.wm.agent.core.constants.LifeCycleStopHookOrdered;
import com.lcsc.wm.agent.core.interceptor.SimpleSpyInterceptorApi;
import com.lcsc.wm.agent.core.properties.MethodInvokeAdvisorProperties;
import com.lcsc.wm.agent.core.vo.AgentStatistics;
import com.lcsc.wm.agent.core.vo.MethodInvokeVO;
import com.lcsc.wm.agent.framework.advisor.AbstractMethodInvokePointcutAdvisor;
import com.lcsc.wm.agent.framework.interceptor.SpyInterceptorApi;
import com.lcsc.wm.agent.framework.vo.ClassMethodInfo;
import com.lcsc.wm.agent.framework.vo.InvokeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-23 23:00
 * @see com.lcsc.wm.agent.core.configuration.advisor.AgentMethodInvokeRegistryPostProcessor
 */
@Slf4j
public class SimpleMethodInvokePointcutAdvisor extends AbstractMethodInvokePointcutAdvisor implements DisposableBean, Ordered {

    private final Map<String, MethodInvokeVO> methodInvokeMap = new ConcurrentHashMap<>();

    private AgentStatistics agentStatistics;

    public SimpleMethodInvokePointcutAdvisor(ClassMethodInfo classMethodInfo) {
        this(classMethodInfo, Boolean.FALSE, SimpleSpyInterceptorApi.class);
    }

    public SimpleMethodInvokePointcutAdvisor(ClassMethodInfo classMethodInfo, Class<? extends SpyInterceptorApi> interceptor) {
        this(classMethodInfo, Boolean.FALSE, interceptor);
    }

    /**
     * 修改这个构造器之前先看注释方法
     *
     * @param classMethodInfo
     * @param canRetransform
     * @see com.lcsc.wm.agent.core.configuration.advisor.BeanDefinitionRegistryUtils#registry(BeanDefinitionRegistry, MethodInvokeAdvisorProperties)
     */
    public SimpleMethodInvokePointcutAdvisor(ClassMethodInfo classMethodInfo, Boolean canRetransform, Class<? extends SpyInterceptorApi> spyInterceptorClass) {
        super(classMethodInfo, canRetransform, spyInterceptorClass);
    }

    @Override
    protected void atBefore(InvokeVO invokeVO) {

        //入栈
        MethodInvokeVO methodInvokeVO = new MethodInvokeVO(getClassMethodInfo().getFullyQualifiedMethodName(), getParams(invokeVO));
        methodInvokeMap.put(getInvokeKey(invokeVO), methodInvokeVO);

        atMethodInvokeBefore(invokeVO, methodInvokeVO);

        agentStatistics.addMethodInvoke(methodInvokeVO);

    }

    protected void atMethodInvokeBefore(InvokeVO invokeVO, MethodInvokeVO methodInvokeVO) {
    }

    @Override
    protected void atExit(InvokeVO invokeVO) {

        //出栈
        if (methodInvokeMap.containsKey(getInvokeKey(invokeVO))) {
            //
            MethodInvokeVO methodInvoke = methodInvokeMap.get(getInvokeKey(invokeVO));
            methodInvoke.initialized();

            atMethodInvokeAfter(invokeVO, methodInvoke);

        } else {

            log.error("Predecessor node not found, Class:{}, Method: {}", invokeVO.getClazz().getName(), invokeVO.getMethodName());

        }

    }

    /**
     * 组件加载完毕
     *
     * @param invokeVO
     * @param methodInvokeVO
     */
    protected void atMethodInvokeAfter(InvokeVO invokeVO, MethodInvokeVO methodInvokeVO) {
    }

    protected Object[] getParams(InvokeVO invokeVO) {
        return invokeVO.getParams();
    }

    protected String getInvokeKey(InvokeVO invokeVO) {
        return String.valueOf(invokeVO.getInvokeId());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        agentStatistics = applicationContext.getBean(AgentStatistics.class);
        Assert.notNull(agentStatistics, "AgentStatistics");
    }

    @Override
    public void destroy() {
        methodInvokeMap.clear();
    }

    @Override
    public int getOrder() {
        return LifeCycleStopHookOrdered.RELEASE_METHOD_INVOKE;
    }

}
