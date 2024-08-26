package com.lcsc.wm.agent.plugin.core.configuration.trubo;

import com.lcsc.wm.agent.core.constants.LifeCycleOrdered;
import com.lcsc.wm.agent.core.lifecycle.AgentLifeCycleHook;

public interface ApplicationTurboLifeCycleHook extends AgentLifeCycleHook {

    @Override
    default int getOrder() {
        return LifeCycleOrdered.AGENT_RETRANSFORM + 1;
    }

}
