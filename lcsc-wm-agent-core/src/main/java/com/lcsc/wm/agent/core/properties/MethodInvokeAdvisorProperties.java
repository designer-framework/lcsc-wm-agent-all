package com.lcsc.wm.agent.core.properties;

import com.lcsc.wm.agent.core.advisor.SimpleMethodInvokePointcutAdvisor;
import com.lcsc.wm.agent.core.interceptor.SimpleSpyInterceptorApi;
import com.lcsc.wm.agent.framework.interceptor.SpyInterceptorApi;
import com.lcsc.wm.agent.framework.vo.ClassMethodInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-28 13:54
 */
@Data
@NoArgsConstructor
public class MethodInvokeAdvisorProperties {

    /**
     * 只能继承SimpleMethodInvokePointcutAdvisor
     */
    private Class<? extends SimpleMethodInvokePointcutAdvisor> pointcutAdvisor = SimpleMethodInvokePointcutAdvisor.class;

    /**
     * 全限定方法名
     * fullyQualifiedMethodName
     */
    private String method;

    /**
     * 是否允许重新加载已被装载的类
     */
    private Boolean canRetransform = Boolean.FALSE;

    private Class<? extends SpyInterceptorApi> interceptor = SimpleSpyInterceptorApi.class;

    public MethodInvokeAdvisorProperties(String method, Boolean canRetransform, Class<? extends SpyInterceptorApi> interceptor) {
        this(SimpleMethodInvokePointcutAdvisor.class, method, canRetransform, interceptor);
    }

    public MethodInvokeAdvisorProperties(Class<? extends SimpleMethodInvokePointcutAdvisor> pointcutAdvisor, String method, Boolean canRetransform, Class<? extends SpyInterceptorApi> interceptor) {
        this.pointcutAdvisor = pointcutAdvisor;
        this.method = method;
        this.canRetransform = canRetransform;
        this.interceptor = interceptor;
    }

    public ClassMethodInfo getMethodInfo() {
        return ClassMethodInfo.create(method);
    }

    @Override
    public String toString() {
        return "{\"method\": \"" + method + '\"' +
                ", \"canRetransform\": \"" + canRetransform + "\"" +
                ", \"interceptor\": \"" + interceptor.getName() + "\"" +
                "}";
    }

}
