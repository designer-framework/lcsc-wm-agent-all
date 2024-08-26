package com.lcsc.wm.agent.framework.lifecycle;

public interface AgentLifeCycle {

    /**
     * 开始分析
     */
    default void start() {
    }

    /**
     * 分析完毕
     */
    void stop();

}
