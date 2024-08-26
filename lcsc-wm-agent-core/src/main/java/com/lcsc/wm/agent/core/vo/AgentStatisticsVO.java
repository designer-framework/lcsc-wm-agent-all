package com.lcsc.wm.agent.core.vo;

import lombok.Setter;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class AgentStatisticsVO implements AgentStatistics {

    protected final Collection<MethodInvokeVO> methodInvokes = new ConcurrentLinkedDeque<>();

    protected final Map<String, Integer> invokeStackTrace = new ConcurrentHashMap<>();

    @Setter
    protected volatile BigDecimal agentTime;

    @Override
    public BigDecimal getAgentTime() {
        return agentTime;
    }

    @Override
    public void addMethodInvoke(MethodInvokeVO methodInvokeVO) {
        methodInvokes.add(methodInvokeVO);
    }

    @Override
    public Collection<MethodInvokeVO> getMethodInvokes() {
        return methodInvokes;
    }

    /**
     * 在线程运算或等待的时间里, 采样的数据会一致, 如果一致则累加1
     */
    @Override
    public void addInvokeTrace(String stackTraceElements) {
        invokeStackTrace.put(stackTraceElements, invokeStackTrace.getOrDefault(stackTraceElements, 0) + 1);
    }

    @Override
    public Map<String, Integer> getInvokeStackTrace() {
        return invokeStackTrace;
    }

}
