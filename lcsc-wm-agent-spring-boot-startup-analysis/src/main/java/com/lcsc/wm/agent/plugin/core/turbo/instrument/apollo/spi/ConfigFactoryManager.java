package com.lcsc.wm.agent.plugin.core.turbo.instrument.apollo.spi;

public interface ConfigFactoryManager {
    ConfigFactory getFactory(String namespace);
}
