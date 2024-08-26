package com.lcsc.wm.agent.core.configuration.env;

import com.lcsc.wm.agent.core.AgentBootstrap;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.File;
import java.security.CodeSource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class AgentHomeEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    static final String PROFILING_JAR_HOME = "spring.agent.output.home";
//    private static final String DEFAULT_PROPERTIES = "defaultProperties";

    /**
     * 比系统环境变量配置加载更早
     *
     * @return
     * @see org.springframework.boot.context.config.ConfigFileApplicationListener
     * @see org.springframework.boot.env.SystemEnvironmentPropertySourceEnvironmentPostProcessor
     */
    @Override
    public int getOrder() {
        return ConfigFileApplicationListener.DEFAULT_ORDER - 1;
    }

    /**
     * @param environment the environment to post-process
     * @param application the application to which the environment belongs
     * @see com.lcsc.wm.agent.core.properties.AgentConfigProperties#getAllowOverridingDefaultProperties()
     */
    @Override
    @SneakyThrows
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        //Agent所在的文件夹
        loadAgentHome(environment, agentHome());

//        Properties properties = new Properties();
//        properties.put("spring.agent.flame-graph.high-precision", "true");
//        environment.getPropertySources().addLast(new PropertiesPropertySource(DEFAULT_PROPERTIES, properties));
    }

    private void loadAgentHome(ConfigurableEnvironment environment, String agentHome) {
        if (StringUtils.isNotBlank(agentHome)) {

            Map<String, Object> copyMap = new HashMap<>();
            copyMap.put(PROFILING_JAR_HOME, agentHome);

            MapPropertySource mapPropertySource = new MapPropertySource("AgentArgsMapPropertySource", copyMap);
            environment.getPropertySources().addFirst(mapPropertySource);

        }
    }

    private String agentHome() {
        CodeSource codeSource = AgentBootstrap.class.getProtectionDomain().getCodeSource();
        if (codeSource != null) {
            try {
                return new File(codeSource.getLocation().toURI().getSchemeSpecificPart()).getParentFile().getAbsolutePath();
            } catch (Throwable e) {
                log.error("try to find agent.home from CodeSource error", e);
            }
        }
        return "";
    }

}
