package com.lcsc.wm.agent.core;

import com.lcsc.wm.agent.core.configuration.AgentAutoConfiguration;
import com.lcsc.wm.agent.core.constants.LifeCycleStopHookOrdered;
import com.lcsc.wm.agent.core.properties.AgentClassLoaderProperties;
import com.lcsc.wm.agent.framework.advisor.PointcutAdvisor;
import com.lcsc.wm.agent.framework.context.AgentContainer;
import lombok.Getter;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;

import java.agent.SpyAPI;
import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@SpringBootApplication(scanBasePackages = "com.lcsc.wm.agent")
public class AgentSpringBootContainer implements AgentContainer, DisposableBean, Ordered {

    private static final List<Runnable> agentShutdownHooks = new ArrayList<>();

    /**
     * 增强的类被调用时会触发埋点 --> 会调用AbstractSpy
     */
    @Autowired
    private SpyAPI.AbstractSpy spyAPI;

    /**
     * 用于判断哪些类需要增强, 增强的类要做什么事
     */
    @Autowired
    private List<PointcutAdvisor> pointcutAdvisor;

    /**
     * 用于判断哪些类需要增强
     */
    @Autowired
    private AgentClassLoaderProperties agentClassLoaderProperties;

    /**
     * 环境变量透传到性能分析容器中
     *
     * @param argsMap jvm配置
     * @return
     * @see org.springframework.boot.SpringApplication#deduceMainApplicationClass()
     */
    public static ConfigurableApplicationContext main(Map<String, String> argsMap, Instrumentation ins) {
        SpringApplication springApplication = new SpringApplication(AgentSpringBootContainer.class);

        Map<String, Object> defaultPropertiesMap = new HashMap<>();
        defaultPropertiesMap.put(AgentAutoConfiguration.INSTRUMENTATION, ins);
        if (argsMap != null) {
            defaultPropertiesMap.putAll(argsMap);
        }

        springApplication.setDefaultProperties(defaultPropertiesMap);
        
        return springApplication.run();
    }

    /**
     * @param argsMap
     * @return
     * @see org.springframework.core.env.SimpleCommandLinePropertySource#SimpleCommandLinePropertySource(String...)
     * 举例(配置a=2会注入到容器): -javaagent:D:\TeamWork\lcsc-wm-agent-all\lcsc-wm-agent-packaging\target\agent-bin\lcsc-wm-agent.jar=;a=2
     */
    private static String[] getCommandLineArgs(Map<String, String> argsMap) {
        if (argsMap == null) {

            return new String[0];

        } else {

            //转换成SpringBoot能识别的环境变量格式
            return argsMap.entrySet().stream()
                    .map(entry -> "--" + entry.getKey() + "=" + entry.getValue())
                    .toArray(String[]::new);

        }
    }

    @Override
    public List<PointcutAdvisor> getPointcutAdvisor() {
        return pointcutAdvisor;
    }

    @Override
    public void addShutdownHook(Runnable runnable) {
        agentShutdownHooks.add(runnable);
    }

    @Override
    public void destroy() throws Exception {
        agentShutdownHooks.forEach(Runnable::run);
    }

    @Override
    public int getOrder() {
        return LifeCycleStopHookOrdered.RELEASE_AGENT;
    }


}
