package com.lcsc.wm.agent.core.advisor;

import com.lcsc.wm.agent.core.hook.AgentLifeCycleStopHook;
import com.lcsc.wm.agent.core.vo.AgentStatistics;
import com.lcsc.wm.agent.core.vo.DurationUtils;
import com.lcsc.wm.agent.core.vo.MethodInvokeVO;
import com.lcsc.wm.agent.framework.lifecycle.AgentLifeCycle;
import com.lcsc.wm.agent.framework.vo.ClassMethodInfo;
import com.lcsc.wm.agent.framework.vo.InvokeVO;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * Spring项目启动耗时分析
 */
@Slf4j
public abstract class AbstractLifeCyclePointcutAdvisor extends SimpleMethodInvokePointcutAdvisor {

    private final AgentLifeCycle agentLifeCycle;

    private final AgentStatistics agentStatistics;

    private BigDecimal startTime = BigDecimal.ZERO;

    public AbstractLifeCyclePointcutAdvisor(ClassMethodInfo classMethodInfo, AgentLifeCycle agentLifeCycle, AgentStatistics agentStatistics) {
        super(classMethodInfo);
        this.agentLifeCycle = agentLifeCycle;
        this.agentStatistics = agentStatistics;
    }

    @Override
    public boolean isReady(InvokeVO invokeVO) {
        return true;
    }

    /**
     * 项目启动时间
     *
     * @param invokeVO
     */
    @Override
    protected void atMethodInvokeBefore(InvokeVO invokeVO, MethodInvokeVO methodInvokeVO) {
        //调用start, 启动性能分析
        agentLifeCycle.start();

        //标记开始分析
        getAgentState().start();

        //性能分析起始时间
        startTime = DurationUtils.nowMillis();
    }

    /**
     * 项目启动完成, 发布分析完成事件
     *
     * @param invokeVO
     * @see AgentLifeCycleStopHook
     */
    @Override
    protected void atMethodInvokeAfter(InvokeVO invokeVO, MethodInvokeVO methodInvokeVO) {
        //性能分析耗时
        agentStatistics.setAgentTime(DurationUtils.nowMillis().subtract(startTime));

        //标记分析完毕
        getAgentState().stop();

        //分析完毕, 通知释放资源,关闭容器,上报分析数据...
        agentLifeCycle.stop();
    }

}
