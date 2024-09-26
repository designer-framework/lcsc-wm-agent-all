package com.lcsc.profiling.web.test.env;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: Designer
 * @date : 2024-09-25 22:23
 */
@Slf4j
@Setter
@Service
public class EnvironmentTestService implements ApplicationContextAware, ApplicationRunner {

    private ApplicationContext applicationContext;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.warn("[EnvironmentTest-SpringWebDemo] ApplicationName: {}, Args: {}", applicationContext.getId(), args.getSourceArgs());
    }

}
