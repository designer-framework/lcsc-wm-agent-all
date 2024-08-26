package com.lcsc.wm.agent.plugin.core.turbo.instrument;

import com.alibaba.bytekit.agent.inst.Instrument;
import com.lcsc.wm.agent.plugin.core.turbo.constants.TurboConstants;
import com.lcsc.wm.agent.plugin.core.turbo.instrument.apollo.Config;
import com.lcsc.wm.agent.plugin.core.turbo.instrument.apollo.ConfigFile;
import com.lcsc.wm.agent.plugin.core.turbo.instrument.apollo.core.enums.ConfigFileFormat;
import com.lcsc.wm.agent.plugin.core.turbo.instrument.apollo.spi.ConfigFactory;
import com.lcsc.wm.agent.plugin.core.turbo.instrument.apollo.spi.ConfigFactoryManager;

import java.util.Map;


/**
 * @see com.ctrip.framework.apollo.internals.DefaultConfigManager
 */
@Instrument(Class = TurboConstants.DefaultConfigManager)
public class DefaultConfigManager {

    private ConfigFactoryManager m_factoryManager;

    private Map<String, Config> m_configs;

    private Map<String, ConfigFile> m_configFiles;

    public Config getConfig(String namespace) {

        Config config = m_configs.get(namespace);

        if (config == null) {
            synchronized (namespace.intern()) {

                config = m_configs.get(namespace);

                if (config == null) {

                    ConfigFactory factory = m_factoryManager.getFactory(namespace);
                    m_configs.put(namespace, config = factory.create(namespace));

                }
            }
        }

        return config;
    }

    public ConfigFile getConfigFile(String namespace, ConfigFileFormat configFileFormat) {
        String namespaceFileName = String.format("%s.%s", namespace, configFileFormat.getValue());
        ConfigFile configFile = m_configFiles.get(namespaceFileName);

        if (configFile == null) {
            synchronized (namespace.intern()) {
                configFile = m_configFiles.get(namespaceFileName);

                if (configFile == null) {
                    ConfigFactory factory = m_factoryManager.getFactory(namespaceFileName);

                    configFile = factory.createConfigFile(namespaceFileName, configFileFormat);
                    m_configFiles.put(namespaceFileName, configFile);
                }
            }
        }

        return configFile;
    }

}

