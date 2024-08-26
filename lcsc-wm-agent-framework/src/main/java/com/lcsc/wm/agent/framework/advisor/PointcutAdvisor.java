package com.lcsc.wm.agent.framework.advisor;

import com.lcsc.wm.agent.framework.pointcut.Pointcut;

/**
 * @description: 切面
 * @author: Designer
 * @date : 2024-07-23 22:40
 */
public interface PointcutAdvisor extends Advisor {

    /**
     * 切点
     *
     * @return
     */
    Pointcut getPointcut();

}
