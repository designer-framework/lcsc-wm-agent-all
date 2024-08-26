package com.lcsc.wm.agent.framework.advisor;

import com.lcsc.wm.agent.framework.advice.Advice;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-23 22:39
 */
public interface Advisor {

    /**
     * 切点调用逻辑
     *
     * @return
     */
    Advice getAdvice();

}
