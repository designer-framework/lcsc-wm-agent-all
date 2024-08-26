package com.lcsc.wm.agent.core.constants;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.Ordered;

public class LifeCycleOrdered {

    //------START------//
    /**
     * 0. 开始性能分析
     */
    public static final Integer AGENT_RETRANSFORM = Ordered.HIGHEST_PRECEDENCE;

    //------STOP------//
    /**
     * 0. 收集数据
     */
    public static final Integer COLLECT_STATISTICS_DATA = Ordered.HIGHEST_PRECEDENCE;
    
    /**
     * 1. 性能分析完毕, 停止采集火焰图数据
     */
    public static final Integer STOP_FLAME_GRAPH_PROFILER = COLLECT_STATISTICS_DATA + 20;

    /**
     * 2. 应用启动完毕, 采集应用基本信息
     */
    public static final Integer SPRING_APPLICATION_INFO = STOP_FLAME_GRAPH_PROFILER + 20;

    /**
     * 3. 上报分析完成的数据
     */
    public static final Integer UPLOAD_STATISTICS = SPRING_APPLICATION_INFO + 20;

    /**
     * 4. 等待组件耗时数据统计完毕
     */
    public static final Integer WAIT_COMPONENTS_STATISTICS = UPLOAD_STATISTICS + 1;

    /**
     * 5. 性能分析完毕, 停止容器, 触发Bean销毁, 释放资源
     *
     * @see DisposableBean#destroy()
     */
    public static final Integer STOP_PROFILER_CONTAINER = WAIT_COMPONENTS_STATISTICS + 20;

}
