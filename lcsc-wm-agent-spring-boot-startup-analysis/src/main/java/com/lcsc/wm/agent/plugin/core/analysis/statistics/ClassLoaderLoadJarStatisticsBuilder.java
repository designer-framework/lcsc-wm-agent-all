package com.lcsc.wm.agent.plugin.core.analysis.statistics;

import com.alibaba.bytekit.utils.ClassLoaderUtils;
import com.lcsc.wm.agent.plugin.core.enums.StatisticsEnum;
import com.lcsc.wm.agent.plugin.core.vo.ClassLoaderJarsVO;
import com.lcsc.wm.agent.plugin.core.vo.SpringAgentStatistics;
import org.springframework.util.ResourceUtils;

import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.security.CodeSource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: Designer
 * @date : 2024-08-04 17:08
 */
public class ClassLoaderLoadJarStatisticsBuilder implements StatisticsBuilder {

    private final Instrumentation instrumentation;

    public ClassLoaderLoadJarStatisticsBuilder(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
    }

    public ClassLoaderJarsVO getClassLoaderNotLoadedJars(ClassLoader classLoader) {

        //key: ClassLoader , value: 暂未使用到的jar包
        for (Map.Entry<ClassLoader, Set<String>> entry : getClassLoaderLoadedJars().entrySet()) {

            if (classLoader == entry.getKey()) {

                URL[] allJar = ClassLoaderUtils.getUrls(entry.getKey());

                Set<ClassLoaderJarsVO.ClassLoaderJarVO> unloadedJar = Arrays.stream(allJar)
                        .map(URL::toString)
                        .filter(url -> !entry.getValue().contains(url))
                        .map(ClassLoaderJarsVO.ClassLoaderJarVO::new)
                        .collect(Collectors.toSet());

                return new ClassLoaderJarsVO(entry.getKey().toString(), unloadedJar, allJar.length, unloadedJar.size(), allJar.length - unloadedJar.size());
            }

        }

        return new ClassLoaderJarsVO(classLoader.toString(), Collections.emptySet(), 0, 0, 0);
    }

    private Map<ClassLoader, Set<String>> getClassLoaderLoadedJars() {
        Map<ClassLoader, Set<String>> classLoaderLoadedJars = new HashMap<>();

        for (Class<?> loadedClass : instrumentation.getAllLoadedClasses()) {

            ClassLoader classLoader = loadedClass.getClassLoader();
            if (classLoader == null) {
                continue;
            }

            //排除特殊类加载器
            if (classLoader.toString().contains("DelegatingClassLoader")
                    || classLoader.toString().contains("ExtClassLoader")
                    || classLoader.toString().contains("AgentClassloader")
            ) {
                continue;
            }

            CodeSource codeSource = loadedClass.getProtectionDomain().getCodeSource();
            if (codeSource == null) {
                continue;
            }

            URL location = codeSource.getLocation();
            //过滤出jar包
            if (location == null || !(location.toString().endsWith(".jar") || ResourceUtils.isJarURL(location))) {
                continue;
            }

            //类加载器 : 该类加载器已加载的jar包
            Set<String> urls = classLoaderLoadedJars.computeIfAbsent(classLoader, ladedJars -> new HashSet<>());
            urls.add(location.toString());
        }

        return classLoaderLoadedJars;
    }

    @Override
    public Object build(SpringAgentStatistics springAgentStatistics) {
        return Arrays.asList(getClassLoaderNotLoadedJars(Thread.currentThread().getContextClassLoader()));
    }

    @Override
    public boolean support(String statisticsType) {
        return StatisticsEnum.unusedJarMap.getType().equals(statisticsType);
    }

}
