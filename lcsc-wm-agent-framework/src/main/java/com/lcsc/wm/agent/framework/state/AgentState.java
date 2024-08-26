package com.lcsc.wm.agent.framework.state;

public interface AgentState {

    /**
     * 开始性能分析
     */
    void start();

    /**
     * 性能分析完毕
     */
    void stop();

    /**
     * 是否已经开始性能分析
     */
    boolean isStarted();

}
