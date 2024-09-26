package com.lcsc.profiling.web.test.copy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.ConverterRegistry;
import cn.hutool.core.util.ClassLoaderUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.util.StopWatch;

import javax.annotation.PostConstruct;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: Designer
 * @date : 2024-09-25 22:23
 */
@Slf4j
public class BeanCopyTestService implements ApplicationRunner {

    /**
     * 拷贝对象中的集合(集合中2万个对象)
     *
     * @param args
     * @see ConverterRegistry#convert(Type, Object, Object, boolean)
     * @see ClassLoaderUtil#doLoadClass(String, ClassLoader, boolean)
     */
    public static void main(String[] args) {
        new BeanCopyTestService().copy();
    }

    @PostConstruct
    public void copy() {
        Source source = new Source();

        List<Source> sources = new ArrayList<>();
        for (int i = 0; i < 3000; i++) {
            sources.add(new Source());
        }

        source.setField02(sources);

        Target target = new Target();
        StopWatch stopWatch = new StopWatch();

        stopWatch.start("spring");
        BeanUtils.copyProperties(source, target);
        stopWatch.stop();

        stopWatch.start("hutool");
        BeanUtil.copyProperties(source, target);
        stopWatch.stop();

        log.error(stopWatch.prettyPrint());
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        copy();
    }

    @Data
    public static class Source {
        private Date field01 = new Date();
        private List<Source> field02;
    }

    @Data
    public static class Target {
        private Date field01;
        private List<Target> field02;
    }

}
