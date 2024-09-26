package com.lcsc.profiling.web.test.service;

import cn.hutool.core.bean.BeanUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: Designer
 * @date : 2024-09-27 02:54
 */
@Slf4j
@Service
public class FirstService implements InitializingBean, SmartInitializingSingleton {

    @Autowired
    private SecondService secondService;

    /**
     * 拷贝对象中的集合(集合中2万个对象)
     *
     * @param args
     * @see cn.hutool.core.convert.ConverterRegistry#convert(java.lang.reflect.Type, java.lang.Object, java.lang.Object, boolean)
     * @see cn.hutool.core.util.ClassLoaderUtil#doLoadClass(java.lang.String, java.lang.ClassLoader, boolean)
     */
    public static void main(String[] args) {
        new FirstService().copy();
    }

    @PostConstruct
    public void copy() {
        Source source = new Source();

        List<Source> sources = new ArrayList<>();
        for (int i = 0; i < 2000; i++) {
            sources.add(new Source());
        }

        source.setField02(sources);

        Target target = new Target();
        StopWatch stopWatch = new StopWatch("BeanCopyTest");

        stopWatch.start("Spring");
        BeanUtils.copyProperties(source, target);
        stopWatch.stop();
        log.warn("SpringTaskTimeMillis: {}/ms", TimeUnit.NANOSECONDS.toMillis(stopWatch.getLastTaskTimeNanos()));

        stopWatch.start("Hutool");
        BeanUtil.copyProperties(source, target);
        stopWatch.stop();
        log.warn("HutoolTaskTimeMillis: {}/ms", TimeUnit.NANOSECONDS.toMillis(stopWatch.getLastTaskTimeNanos()));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        copy();
    }

    @Override
    public void afterSingletonsInstantiated() {
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
