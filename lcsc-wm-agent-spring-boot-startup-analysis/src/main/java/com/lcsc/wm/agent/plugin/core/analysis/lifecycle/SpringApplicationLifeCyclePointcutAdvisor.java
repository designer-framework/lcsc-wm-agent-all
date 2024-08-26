package com.lcsc.wm.agent.plugin.core.analysis.lifecycle;

import com.lcsc.wm.agent.core.advisor.AbstractLifeCyclePointcutAdvisor;
import com.lcsc.wm.agent.core.vo.MethodInvokeVO;
import com.lcsc.wm.agent.framework.enums.InvokeType;
import com.lcsc.wm.agent.framework.lifecycle.AgentLifeCycle;
import com.lcsc.wm.agent.framework.vo.ClassMethodInfo;
import com.lcsc.wm.agent.framework.vo.InvokeVO;
import com.lcsc.wm.agent.plugin.core.events.SpringBootApplicationLabelEvent;
import com.lcsc.wm.agent.plugin.core.vo.SpringAgentStatistics;
import com.lcsc.wm.agent.plugin.core.vo.SpringBootApplicationInfo;
import com.lcsc.wm.agent.plugin.core.vo.SpringLabelEnum;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Spring项目启动耗时分析
 */
@Slf4j
public class SpringApplicationLifeCyclePointcutAdvisor extends AbstractLifeCyclePointcutAdvisor {

    private final SpringBootApplicationInfo springBootApplicationInfo;

    public SpringApplicationLifeCyclePointcutAdvisor(
            ClassMethodInfo classMethodInfo
            , AgentLifeCycle agentLifeCycles
            , SpringAgentStatistics springAgentStatistics
            , SpringBootApplicationInfo springBootApplicationInfo
    ) {
        super(classMethodInfo, agentLifeCycles, springAgentStatistics);
        this.springBootApplicationInfo = springBootApplicationInfo;
    }

    @SneakyThrows
    @Override
    protected void atMethodInvokeBefore(InvokeVO invokeVO, MethodInvokeVO methodInvokeVO) {
        super.atMethodInvokeBefore(invokeVO, methodInvokeVO);
        //SpringBoot版本
        applicationEventPublisher.publishEvent(
                SpringBootApplicationLabelEvent.create(this, SpringLabelEnum.SPRING_BOOT_VERSION, getSpringBootVersion(invokeVO.getLoader()))
        );
    }

    @Override
    protected void atMethodInvokeAfter(InvokeVO invokeVO, MethodInvokeVO methodInvokeVO) {
        super.atMethodInvokeAfter(invokeVO, methodInvokeVO);
        if (invokeVO.getInvokeType() == InvokeType.EXCEPTION) {
            return;
        }
        //SpringBoot项目启动耗时
        applicationEventPublisher.publishEvent(
                SpringBootApplicationLabelEvent.create(this, SpringLabelEnum.START_UP_TIME, methodInvokeVO.getDuration().divide(BigDecimal.valueOf(1000), RoundingMode.UP) + "秒")
        );
        //用户的SpringBoot项目实例
        applicationEventPublisher.publishEvent(
                SpringBootApplicationLabelEvent.create(this, SpringLabelEnum.SPRING_APPLICATION, invokeVO.getReturnObj())
        );
    }

    private String getSpringBootVersion(ClassLoader classLoader) throws Exception {
        return String.valueOf(classLoader.loadClass("org.springframework.boot.SpringBootVersion").getMethod("getVersion").invoke(null));
    }

}
