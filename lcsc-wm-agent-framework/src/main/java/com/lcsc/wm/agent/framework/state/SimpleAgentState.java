package com.lcsc.wm.agent.framework.state;

public class SimpleAgentState implements AgentState {

    /**
     * -- GETTER --
     * 开始性能分析
     */
    private volatile boolean started;

    @Override
    public void start() {
        started = true;
    }

    @Override
    public void stop() {
        started = false;
    }


    @Override
    public boolean isStarted() {
        return started;
    }

}
