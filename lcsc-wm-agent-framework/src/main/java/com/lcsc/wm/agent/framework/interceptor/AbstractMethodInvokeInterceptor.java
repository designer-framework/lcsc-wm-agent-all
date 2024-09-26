package com.lcsc.wm.agent.framework.interceptor;

import com.lcsc.wm.agent.framework.vo.InvokeVO;
import lombok.extern.slf4j.Slf4j;

/**
 * 参见
 * {@link com.lcsc.wm.agent.core.AdviceListenerAdapter}
 */
@Slf4j
public abstract class AbstractMethodInvokeInterceptor implements InvokeInterceptor {

    public abstract boolean isReady(InvokeVO invokeVO);

    @Override
    public void before(InvokeVO invokeVO) throws Throwable {
        if (isReady(invokeVO)) {
            atBefore(invokeVO);
        }
    }

    protected abstract void atBefore(InvokeVO invokeVO) throws Throwable;

    @Override
    public void afterReturning(InvokeVO invokeVO) throws Throwable {
        if (isReady(invokeVO)) {
            atAfterReturning(invokeVO);
        }
    }

    protected void atAfterReturning(InvokeVO invokeVO) throws Throwable {
        atExit(invokeVO);
    }

    @Override
    public final void afterThrowing(InvokeVO invokeVO) {
        if (isReady(invokeVO)) {
            atAfterThrowing(invokeVO);
        }
    }

    protected void atAfterThrowing(InvokeVO invokeVO) {
        onThrowing(invokeVO);
        log.warn("调用异常, Class:{}, Method: {}", invokeVO.getClazz().getName(), invokeVO.getMethodName(), invokeVO.getThrowExp());
    }

    protected void onThrowing(InvokeVO invokeVO) {
    }

    protected abstract void atExit(InvokeVO invokeVO) throws Throwable;

}
