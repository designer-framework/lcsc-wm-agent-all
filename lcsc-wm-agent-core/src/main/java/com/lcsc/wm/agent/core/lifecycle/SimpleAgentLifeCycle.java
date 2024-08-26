package com.lcsc.wm.agent.core.lifecycle;

import com.lcsc.wm.agent.framework.lifecycle.AgentLifeCycle;
import com.lcsc.wm.agent.framework.state.AgentState;

import java.util.List;

public class SimpleAgentLifeCycle implements AgentLifeCycle {

    private final List<AgentLifeCycleHook> agentLifeCycleHooks;

    private final AgentState agentState;

    public SimpleAgentLifeCycle(List<AgentLifeCycleHook> agentLifeCycleHooks, AgentState agentState) {
        this.agentLifeCycleHooks = agentLifeCycleHooks;
        this.agentState = agentState;
    }

    @Override
    public void start() {
        agentLifeCycleHooks.forEach(AgentLifeCycleHook::start);
        agentState.start();
    }

    @Override
    public void stop() {
        agentState.stop();
        agentLifeCycleHooks.forEach(AgentLifeCycleHook::stop);
    }

}
