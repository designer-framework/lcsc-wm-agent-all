package com.lcsc.wm.agent.core.advisor;

import com.lcsc.wm.agent.framework.interceptor.SpyInterceptorApi;
import com.lcsc.wm.agent.framework.vo.ClassMethodInfo;
import com.lcsc.wm.agent.framework.vo.InvokeVO;

/**
 * @description:
 * @author: Designer
 * @date : 2024-09-27 00:13
 */
public class IgnoreExceptionMethodInvokePointcutAdvisor extends SimpleMethodInvokePointcutAdvisor {

    public IgnoreExceptionMethodInvokePointcutAdvisor(ClassMethodInfo classMethodInfo, Boolean canRetransform, Class<? extends SpyInterceptorApi> spyInterceptorClass) {
        super(classMethodInfo, canRetransform, spyInterceptorClass);
    }

    @Override
    public void atAfterThrowing(InvokeVO invokeVO) {
        this.atExit(invokeVO);
    }

}
