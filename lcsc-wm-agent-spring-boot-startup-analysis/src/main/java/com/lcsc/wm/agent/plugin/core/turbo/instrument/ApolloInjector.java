package com.lcsc.wm.agent.plugin.core.turbo.instrument;

import com.alibaba.bytekit.agent.inst.Instrument;
import com.alibaba.bytekit.agent.inst.InstrumentApi;
import com.lcsc.wm.agent.plugin.core.turbo.constants.TurboConstants;

@Instrument(Class = TurboConstants.ApolloInjector)
public class ApolloInjector {

    /**
     * @param clazz
     * @param <T>
     * @return
     * @see com.ctrip.framework.apollo.build.ApolloInjector#getInstance(Class)
     */
    public static <T> T getInstance(Class<T> clazz) {

        if (TurboConstants.ConfigManager.equals(clazz.getName())) {

            try {

                Class<T> defaultConfigManager = (Class<T>) Class.forName(TurboConstants.DefaultConfigManager, true, clazz.getClassLoader());
                return defaultConfigManager.newInstance();

            } catch (Exception e) {
                //替换失败
                return InstrumentApi.invokeOrigin();
            }

        } else {

            return InstrumentApi.invokeOrigin();

        }
    }

}
