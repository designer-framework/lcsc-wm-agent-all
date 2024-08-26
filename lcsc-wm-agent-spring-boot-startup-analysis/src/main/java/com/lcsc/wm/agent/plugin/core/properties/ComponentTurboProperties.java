package com.lcsc.wm.agent.plugin.core.properties;

import com.lcsc.wm.agent.plugin.core.configuration.trubo.SpringComponentTurboAutoConfiguration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @see SpringComponentTurboAutoConfiguration
 */
@Data
@ConfigurationProperties(prefix = "spring.agent.turbo")
public class ComponentTurboProperties {

    private boolean enabledByDefault;

    @NestedConfigurationProperty
    private Enabled swagger;

    @NestedConfigurationProperty
    private Enabled openFeign;

    @NestedConfigurationProperty
    private Enabled apollo;

    @NestedConfigurationProperty
    private Enabled aop;

    @NestedConfigurationProperty
    private Enabled forkJoin;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class Enabled {

        private boolean enabled;

    }

}
