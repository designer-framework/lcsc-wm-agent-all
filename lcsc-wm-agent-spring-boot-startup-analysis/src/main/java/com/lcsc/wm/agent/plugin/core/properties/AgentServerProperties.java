package com.lcsc.wm.agent.plugin.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "server")
public class AgentServerProperties {

    private Integer port;

}
