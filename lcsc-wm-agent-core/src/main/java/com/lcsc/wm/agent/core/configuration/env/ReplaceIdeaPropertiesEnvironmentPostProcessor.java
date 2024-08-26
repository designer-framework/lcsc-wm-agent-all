package com.lcsc.wm.agent.core.configuration.env;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.support.LiveBeansView;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

public class ReplaceIdeaPropertiesEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        //覆盖IDEA自动追加在环境变量中的Jmx配置, 避免项目启动失败
        Map<String, Object> jmxMapPropertySource = new HashMap<>();
        jmxMapPropertySource.put("spring.jmx.enabled", "false");
        jmxMapPropertySource.put("spring.application.admin.enabled", "false");
        jmxMapPropertySource.put(LiveBeansView.MBEAN_DOMAIN_PROPERTY_NAME, environment.resolvePlaceholders("${spring.application.name:lcsc-wm-agent-application}"));
        environment.getPropertySources()
                .addFirst(new MapPropertySource("ReplaceIdeaPropertiesPropertySource", jmxMapPropertySource));


    }

}
