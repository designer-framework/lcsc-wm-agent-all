package com.lcsc.wm.agent.plugin.core.configuration.trubo;

import com.lcsc.wm.agent.plugin.core.annotation.ConditionalOnTurboPropCondition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnTurboPropCondition(pluginName = "swagger")
public class SwaggerTurboConfiguration {

    private static final String SPRINGFOX_DOCUMENTATION_AUTO_STARTUP = "springfox.documentation.auto-startup";

    @Bean
    ApplicationTurboLifeCycleHook swaggerAgentLifeCycleHook() {
        return new ApplicationTurboLifeCycleHook() {
            @Override
            public void start() {
                System.setProperty(SPRINGFOX_DOCUMENTATION_AUTO_STARTUP, "false");
            }
        };
    }

}
