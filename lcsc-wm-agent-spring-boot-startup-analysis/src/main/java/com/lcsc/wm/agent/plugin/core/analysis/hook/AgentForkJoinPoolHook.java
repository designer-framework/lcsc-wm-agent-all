package com.lcsc.wm.agent.plugin.core.analysis.hook;

import com.lcsc.wm.agent.plugin.core.configuration.trubo.ApplicationTurboLifeCycleHook;
import lombok.extern.slf4j.Slf4j;

/**
 * Spring项目启动耗时分析
 */
@Slf4j
public class AgentForkJoinPoolHook implements ApplicationTurboLifeCycleHook {

    private static final String parallelism = "java.util.concurrent.ForkJoinPool.common.parallelism";

    @Override
    public void start() {
        //最少启用2*CPU + 1个线程
        System.setProperty(parallelism,
                String.valueOf(
                        Math.max(Integer.parseInt(System.getProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "-1")), Runtime.getRuntime().availableProcessors() * 2 + 1)
                )
        );
    }

}
