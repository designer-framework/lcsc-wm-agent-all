package com.lcsc.wm.agent.core.properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-10 22:25
 */
@Data
@Slf4j
public class AgentFlameGraphProperties implements InitializingBean {

    private boolean enabled = false;

    private boolean highPrecision;

    /**
     * 被采样线程名
     */
    private Set<String> names = new HashSet<>(Collections.singletonList("main"));

    /**
     * 采样间隔, 值越小精度越高
     */
    private long interval = 1;

    /**
     * 需要高亮显示包名
     */
    private String[] highlightPackage = new String[]{};

    /**
     * 采样噪点(当前批次采样如果出现该关键字, 该帧栈之后的帧栈会被丢弃)
     */
    private String[] skipTrace = new String[]{"java.agent", "com.lcsc.wm.agent"};

    @Override
    public void afterPropertiesSet() throws Exception {
        if (highPrecision) {
            if (interval != 1) {
                log.warn("Flame map with high-precision sampling mode enabled, method call statistics turned off. & interval: {} -> 1", interval);
                interval = 1;
            } else {
                log.warn("Flame map with high-precision sampling mode enabled, method call statistics turned off.");
            }
        }
    }

}
