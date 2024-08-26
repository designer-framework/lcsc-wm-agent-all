package com.lcsc.wm.agent.core.configuration.env;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
public class AgentConfigFileEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final String allowOverridingDefaultProperties = "spring.agent.allow-overriding-default-properties";

    /**
     * 比系统环境变量配置加载更早
     *
     * @return
     * @see ConfigFileApplicationListener
     * @see org.springframework.boot.env.SystemEnvironmentPropertySourceEnvironmentPostProcessor
     */
    @Override
    public int getOrder() {
        return ConfigFileApplicationListener.DEFAULT_ORDER + 1;
    }

    /**
     * @param environment the environment to post-process
     * @param application the application to which the environment belongs
     * @see com.lcsc.wm.agent.core.properties.AgentConfigProperties#getAllowOverridingDefaultProperties()
     */
    @Override
    @SneakyThrows
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        //从Agent目录读取application.yml配置文件
        loadAgentConfigurationProperties(environment, environment.getRequiredProperty(AgentHomeEnvironmentPostProcessor.PROFILING_JAR_HOME));
    }

    private void loadAgentConfigurationProperties(ConfigurableEnvironment environment, String agentHome) throws IOException {
        File location = new File(agentHome, "application-agent.yml");

        if (location.exists()) {

            //解析配置文件
            List<PropertySource<?>> propertySourceList = new YamlPropertySourceLoader()
                    .load(location.getAbsolutePath(), new FileSystemResource(location));

            propertySourceList.forEach(propertySource -> {

                /**
                 * <pre>
                 * ${AgentHome}/application.yml > System Env > System Properties > classpath:/application.yml
                 * application-agent.yml 提供一个配置项，可以反转优先级。 spring.agent.allow-overriding-default-properties=true
                 *
                 * </pre>
                 */
                String allowPropertiesOverriding = String.valueOf(propertySource.getProperty(allowOverridingDefaultProperties));
                //允许重写默认配置
                if (Boolean.parseBoolean(allowPropertiesOverriding)) {
                    environment.getPropertySources().addBefore("applicationConfig: [classpath:/application-plugins.yml]", propertySource);
                    //不允许重写默认配置
                } else {
                    environment.getPropertySources().addFirst(propertySource);
                }

            });

        }
    }

}
