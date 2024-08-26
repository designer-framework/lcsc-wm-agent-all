package com.lcsc.wm.agent.plugin.core.analysis.component;

import com.lcsc.wm.agent.plugin.core.enums.SpringComponentEnum;
import com.lcsc.wm.agent.plugin.core.events.ComponentRootInitializedEvent;
import com.lcsc.wm.agent.plugin.core.vo.InitializedComponent;
import com.lcsc.wm.agent.plugin.core.vo.SpringAgentStatistics;
import com.lcsc.wm.agent.framework.vo.ClassMethodInfo;
import com.lcsc.wm.agent.framework.vo.InvokeVO;
import com.lcsc.wm.agent.core.lifecycle.AgentLifeCycleHook;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * 只是为了统计加载了哪些名命空间
 */
@Slf4j
public class ClassPathScanningCandidateComponentPointcutAdvisor extends AbstractComponentChildCreatorPointcutAdvisor implements AgentLifeCycleHook {

    /**
     * @return
     * @see org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider#findCandidateComponents(java.lang.String)
     */
    public ClassPathScanningCandidateComponentPointcutAdvisor(SpringComponentEnum springComponentEnum, ClassMethodInfo classMethodInfo, SpringAgentStatistics springAgentStatistics) {
        super(springComponentEnum, classMethodInfo);
    }

    /**
     * @see org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider#ClassPathScanningCandidateComponentProvider(boolean)
     */
    @Override
    public void start() {
        super.start();
        applicationEventPublisher.publishEvent(new ComponentRootInitializedEvent(
                this, InitializedComponent.root(SpringComponentEnum.CLASS_PATH_SCANNING_CANDIDATE, BigDecimal.ZERO, true)
        ));

    }

    @Override
    protected String childName(InvokeVO invokeVO) {
        return String.valueOf(invokeVO.getParams()[0]);
    }

}
