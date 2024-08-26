package com.lcsc.wm.agent.core.configuration.instrument;

import lombok.Data;

@Data
public class RetransformAttribute {

    /**
     * 目标样
     * <p>
     * {@link com.alibaba.bytekit.agent.inst.Instrument 类上必须添加该注解}
     */
    private Class<?> instrumentClass;

}
