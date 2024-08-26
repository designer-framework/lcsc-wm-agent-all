package com.lcsc.wm.agent.core.instrument;

import com.alibaba.bytekit.agent.inst.Instrument;
import com.alibaba.bytekit.agent.inst.InstrumentApi;

/**
 * @author hengyunabc 2020-11-30
 * @see java.lang.ClassLoader#loadClass(String)
 */
@Instrument(Class = "java.lang.ClassLoader")
public abstract class ClassLoader_Instrument {

    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (name.startsWith("java.agent.")) {
            ClassLoader extClassLoader = ClassLoader.getSystemClassLoader().getParent();
            if (extClassLoader != null) {
                return extClassLoader.loadClass(name);
            }
        }

        Class clazz = InstrumentApi.invokeOrigin();
        return clazz;
    }

}
