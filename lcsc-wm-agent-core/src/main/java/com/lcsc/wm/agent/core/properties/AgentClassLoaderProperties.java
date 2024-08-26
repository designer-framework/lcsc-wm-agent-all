package com.lcsc.wm.agent.core.properties;

import lombok.Data;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-10 22:25
 */
@Data
public class AgentClassLoaderProperties {

    /**
     * 哪些类加载器需要增强
     */
    private Set<String> enhanceLoaders = new HashSet<>(Collections.singletonList("java.lang.ClassLoader"));

}
