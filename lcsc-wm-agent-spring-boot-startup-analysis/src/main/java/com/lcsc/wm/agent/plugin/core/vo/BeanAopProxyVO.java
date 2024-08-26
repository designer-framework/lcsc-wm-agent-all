package com.lcsc.wm.agent.plugin.core.vo;

import java.io.Serializable;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-12 01:10
 */
public class BeanAopProxyVO implements Serializable {

    /**
     * 当前bean的名字
     */
    private final String name;

    /**
     * 加载耗时
     */
    private final long createProxyLoadMillis;

    public BeanAopProxyVO(String name, long createProxyLoadMillis) {
        this.name = name;
        this.createProxyLoadMillis = createProxyLoadMillis;
    }

    public String getName() {
        return name;
    }

    public long getCreateProxyLoadMillis() {
        return createProxyLoadMillis;
    }

}
