package com.lcsc.wm.agent.plugin.core.turbo.instrument.aop;


import com.alibaba.bytekit.agent.inst.Instrument;

@Instrument(Class = {"org.aspectj.apache.bcel.util.ClassLoaderRepository"})
public class TurboClassLoaderRepository {

    public void clear() {
    }

}
