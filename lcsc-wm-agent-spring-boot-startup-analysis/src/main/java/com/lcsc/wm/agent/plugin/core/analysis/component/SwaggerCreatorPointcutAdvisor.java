package com.lcsc.wm.agent.plugin.core.analysis.component;

import com.lcsc.wm.agent.core.vo.MethodInvokeVO;
import com.lcsc.wm.agent.framework.interceptor.SpyInterceptorApi;
import com.lcsc.wm.agent.framework.vo.ClassMethodInfo;
import com.lcsc.wm.agent.framework.vo.InvokeVO;
import com.lcsc.wm.agent.plugin.core.enums.ComponentEnum;
import com.lcsc.wm.agent.plugin.core.enums.SpringComponentEnum;
import com.lcsc.wm.agent.plugin.core.events.ComponentRootInitializedEvent;
import com.lcsc.wm.agent.plugin.core.vo.InitializedComponent;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * Spring项目启动耗时分析
 */
@Slf4j
public class SwaggerCreatorPointcutAdvisor extends AbstractComponentChildCreatorPointcutAdvisor {

    /**
     * @return
     * @see springfox.documentation.spring.web.plugins.DocumentationPluginsBootstrapper#SPRINGFOX_DOCUMENTATION_AUTO_STARTUP
     */
    public SwaggerCreatorPointcutAdvisor(
            ComponentEnum componentEnum,
            ClassMethodInfo classMethodInfo, Class<? extends SpyInterceptorApi> interceptor
    ) {
        super(componentEnum, classMethodInfo, interceptor);
    }

    @Override
    public void start() {
        super.start();
        applicationEventPublisher.publishEvent(new ComponentRootInitializedEvent(
                this, InitializedComponent.root(SpringComponentEnum.SWAGGER, BigDecimal.ZERO, true)
        ));
    }

    @Override
    protected void atMethodInvokeAfter(InvokeVO invokeVO, MethodInvokeVO methodInvokeVO) {
        super.atMethodInvokeAfter(invokeVO, methodInvokeVO);
    }

    @Override
    protected String childName(InvokeVO invokeVO) {
        return getClassMethodInfo().getFullyQualifiedMethodName();
    }

}
