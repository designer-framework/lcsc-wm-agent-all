package com.lcsc.wm.agent.core.vo;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-26 21:05
 */
public interface AgentStatistics {

    /**
     * 本次插桩总时长
     *
     * @return
     */
    BigDecimal getAgentTime();

    void setAgentTime(BigDecimal agentTime);

    void addMethodInvoke(MethodInvokeVO methodInvokeVO);

    Collection<MethodInvokeVO> getMethodInvokes();

    /**
     * 可以同时分析多个线程的火焰图
     *
     * @param stackTraceElements
     */
    void addInvokeTrace(String stackTraceElements);

    Map<String, Integer> getInvokeStackTrace();

}
