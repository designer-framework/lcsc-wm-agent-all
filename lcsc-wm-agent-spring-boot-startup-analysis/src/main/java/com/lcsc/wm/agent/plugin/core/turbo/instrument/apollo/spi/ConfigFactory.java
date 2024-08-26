package com.lcsc.wm.agent.plugin.core.turbo.instrument.apollo.spi;

import com.lcsc.wm.agent.plugin.core.turbo.instrument.apollo.Config;
import com.lcsc.wm.agent.plugin.core.turbo.instrument.apollo.ConfigFile;
import com.lcsc.wm.agent.plugin.core.turbo.instrument.apollo.core.enums.ConfigFileFormat;

public interface ConfigFactory {

    /**
     * Create the config instance for the namespace.
     *
     * @param namespace the namespace
     * @return the newly created config instance
     */
    Config create(String namespace);

    /**
     * Create the config file instance for the namespace
     *
     * @param namespace the namespace
     * @return the newly created config file instance
     */
    ConfigFile createConfigFile(String namespace, ConfigFileFormat configFileFormat);

}
