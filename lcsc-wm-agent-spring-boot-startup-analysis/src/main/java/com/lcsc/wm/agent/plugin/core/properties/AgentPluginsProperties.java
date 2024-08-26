package com.lcsc.wm.agent.plugin.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-10 22:25
 */
@Data
@ConfigurationProperties(prefix = "spring.agent.plugins")
public class AgentPluginsProperties {

    private ScanningPluginProperties scanning;

}
