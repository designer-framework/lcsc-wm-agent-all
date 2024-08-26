package com.lcsc.wm.agent.core.lifecycle;

import org.springframework.core.Ordered;

/**
 * 性能分析钩子
 */
public interface AgentLifeCycleHook extends Ordered {

    /**
     * 开始分析
     */
    default void start() {
    }

    /**
     * 分析完毕
     */
    default void stop() {
    }

}
