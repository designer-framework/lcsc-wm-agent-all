package com.lcsc.profiling.web.test.env;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Setter
@Slf4j
@Service
public class EnvTestService implements ApplicationRunner, EnvironmentAware, ApplicationContextAware {

    private Environment environment;

    private ApplicationContext applicationContext;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.error("[EnvironmentTest-SpringWebDemo] ApplicationName: {}, Args: {}", applicationContext.getId(), args.getSourceArgs());
    }

}
