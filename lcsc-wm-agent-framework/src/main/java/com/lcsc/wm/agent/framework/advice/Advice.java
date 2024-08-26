package com.lcsc.wm.agent.framework.advice;

import com.lcsc.wm.agent.framework.vo.InvokeVO;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-23 22:34
 */
public interface Advice {

    /**
     * 前置通知
     *
     * @param invokeVO
     * @throws Throwable
     */
    void before(InvokeVO invokeVO) throws Throwable;

    /**
     * 返回通知
     *
     * @param invokeVO
     * @throws Throwable
     */
    void afterReturning(InvokeVO invokeVO) throws Throwable;

    /**
     * 异常通知
     *
     * @param invokeVO
     * @throws Throwable
     */
    void afterThrowing(InvokeVO invokeVO) throws Throwable;

}
