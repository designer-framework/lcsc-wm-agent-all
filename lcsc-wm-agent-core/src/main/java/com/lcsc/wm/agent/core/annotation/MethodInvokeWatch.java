package com.lcsc.wm.agent.core.annotation;

import com.lcsc.wm.agent.core.advisor.SimpleMethodInvokePointcutAdvisor;
import com.lcsc.wm.agent.core.interceptor.SimpleSpyInterceptorApi;
import com.lcsc.wm.agent.framework.advisor.AbstractMethodInvokePointcutAdvisor;
import com.lcsc.wm.agent.framework.interceptor.SpyInterceptorApi;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodInvokeWatch {

    Class<? extends AbstractMethodInvokePointcutAdvisor> pointcutAdvisor() default SimpleMethodInvokePointcutAdvisor.class;

    String value();

    Class<? extends SpyInterceptorApi> interceptor() default SimpleSpyInterceptorApi.class;

    boolean canRetransform() default false;

}
