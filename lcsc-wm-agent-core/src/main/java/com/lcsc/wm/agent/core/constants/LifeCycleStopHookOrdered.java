package com.lcsc.wm.agent.core.constants;

import org.springframework.core.Ordered;

/**
 *
 */
public class LifeCycleStopHookOrdered {

    // -------------      启动性能报告服务器      ------------- //


    // -------------释放性能分析过程中占用的内存资源, 最后启动性能分析报告Server------------- //
    /**
     *
     */
    public static final Integer RELEASE_METHOD_INVOKE = Ordered.HIGHEST_PRECEDENCE + 20;

    /**
     * 释放Agent钩子资源
     */
    public static final Integer RELEASE_AGENT = RELEASE_METHOD_INVOKE + 20;

    /**
     * 1. 异步启动分析报告Http服务器
     */
    public static final Integer START_REPORTER_SERVER = RELEASE_AGENT + 20;

}
