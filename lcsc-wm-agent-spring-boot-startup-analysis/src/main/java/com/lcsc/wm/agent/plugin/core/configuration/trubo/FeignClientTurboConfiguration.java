package com.lcsc.wm.agent.plugin.core.configuration.trubo;


import com.lcsc.wm.agent.plugin.core.annotation.ConditionalOnTurboPropCondition;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnTurboPropCondition(pluginName = "open-feign")
public class FeignClientTurboConfiguration {
}
