package com.lcsc.wm.agent.plugin.extension.environment;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

@Setter
@Slf4j
public class EnvironmentService implements ApplicationRunner, EnvironmentAware, ApplicationContextAware {

    private Environment environment;

    private ApplicationContext applicationContext;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.error("[EnvironmentTest-SpringAgent] ApplicationName: {}, Args: {}", applicationContext.getId(), args.getSourceArgs());
    }

}
