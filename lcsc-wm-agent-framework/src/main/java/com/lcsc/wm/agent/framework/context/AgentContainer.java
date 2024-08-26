package com.lcsc.wm.agent.framework.context;

import com.lcsc.wm.agent.framework.advisor.PointcutAdvisor;

import java.agent.SpyAPI;
import java.util.List;

public interface AgentContainer {

    SpyAPI.AbstractSpy getSpyAPI();

    List<PointcutAdvisor> getPointcutAdvisor();

    void addShutdownHook(Runnable runnable);

}
