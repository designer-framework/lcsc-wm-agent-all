package com.lcsc.wm.agent.core;

import com.alibaba.bytekit.asm.instrument.InstrumentConfig;
import com.alibaba.bytekit.asm.instrument.InstrumentParseResult;
import com.alibaba.bytekit.asm.instrument.InstrumentTransformer;
import com.alibaba.bytekit.asm.matcher.SimpleClassMatcher;
import com.alibaba.bytekit.utils.AsmUtils;
import com.alibaba.bytekit.utils.IOUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.lcsc.wm.agent.core.instrument.ClassLoader_Instrument;
import com.lcsc.wm.agent.core.instrument.EnhanceProfilingInstrumentTransformer;
import com.lcsc.wm.agent.core.properties.AgentClassLoaderProperties;
import com.lcsc.wm.agent.core.transformer.TransformerManager;
import com.lcsc.wm.agent.core.utils.FeatureCodec;
import com.lcsc.wm.agent.core.utils.InstrumentationUtils;
import com.lcsc.wm.agent.framework.advisor.PointcutAdvisor;
import com.lcsc.wm.agent.framework.pointcut.Pointcut;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;

import java.agent.SpyAPI;
import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.Method;
import java.security.CodeSource;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.stream.Collectors;


/**
 * @author vlinux on 15/5/2.
 * @author hengyunabc
 * <p>
 * 保留了Arthas部分代码结构
 */
@Slf4j
public class AgentBootstrap {

    private static final String AGENT_SPY_JAR = "lcsc-wm-agent-spy.jar";

    private static AgentBootstrap agentBootstrap;

    private final TransformerManager transformerManager;

    private final Instrumentation instrumentation;

    private final Thread shutdown;

    private AgentSpringBootContainer agentSpringBootContainer;

    private InstrumentTransformer classLoaderInstrumentTransformer;

    /**
     * 插桩启动类
     *
     * @param instrumentation
     * @param args
     * @throws Throwable
     */
    private AgentBootstrap(Instrumentation instrumentation, Map<String, String> args) throws Throwable {
        this.instrumentation = instrumentation;

        transformerManager = new TransformerManager(instrumentation);

        // 初始化序列化工具
        initFastjson();

        // 1. initSpy(), 先加载Spy避免加载Spring性能分析容器时出现类找不到异常
        initSpy();

        // 2. 启动Agent容器
        initAgentContainer(args, instrumentation);

        // 4. 增强ClassLoader
        enhanceClassLoader();

        // 5. 增强待分析的类
        enhanceNormalClass();

        // 6. hooker
        shutdown = new Thread("spring-agent-shutdown-hooker") {

            @Override
            public void run() {
                AgentBootstrap.this.destroy();
            }
        };

        Runtime.getRuntime().addShutdownHook(shutdown);
    }

    /**
     * 单例
     *
     * @param instrumentation JVM增强
     * @throws Throwable
     */
    public synchronized static AgentBootstrap getInstance(Instrumentation instrumentation, String args) throws Throwable {
        if (agentBootstrap != null) {
            return agentBootstrap;
        }
        //将Agent命令行参数解析成Map格式,
        return getInstance(instrumentation, FeatureCodec.DEFAULT_COMMANDLINE_CODEC.toMap(args));
    }

    /**
     * 单例
     *
     * @param instrumentation JVM增强
     * @throws Throwable
     */
    public synchronized static AgentBootstrap getInstance(Instrumentation instrumentation, Map<String, String> args) throws Throwable {
        if (agentBootstrap == null) {
            agentBootstrap = new AgentBootstrap(instrumentation, args);
        }
        return agentBootstrap;
    }

    private void enhanceNormalClass() {
        //获取Spy实现类
        SpyAPI.setSpy(agentSpringBootContainer.getSpyAPI());

        EnhanceProfilingInstrumentTransformer enhanceProfilingInstrumentTransformer = new EnhanceProfilingInstrumentTransformer(agentSpringBootContainer.getPointcutAdvisor());
        instrumentation.addTransformer(enhanceProfilingInstrumentTransformer, true);

        //
        InstrumentationUtils.triggerRetransformClasses(
                instrumentation
                , agentSpringBootContainer.getPointcutAdvisor().stream()
                        .map(PointcutAdvisor::getPointcut).filter(Pointcut::getCanRetransform).collect(Collectors.toList())
        );
    }

    private void initFastjson() {
        // disable  fastjson circular reference feature
        //JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.DisableCircularReferenceDetect.getMask();
        // add date format option for  fastjson
        JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.WriteDateUseDateFormat.getMask();
        // ignore getter error #1661
        JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.IgnoreErrorGetter.getMask();
        // #2081
        JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.WriteNonStringKeyAsString.getMask();
    }

    private void initSpy() throws Throwable {
        // TODO init SpyImpl ?

        // 将Spy添加到BootstrapClassLoader
        ClassLoader parent = ClassLoader.getSystemClassLoader().getParent();
        Class<?> spyClass = null;
        if (parent != null) {
            try {
                spyClass = parent.loadClass("java.agent.SpyAPI");
            } catch (Throwable e) {
                // ignore
            }
        }

        if (spyClass == null) {

            CodeSource codeSource = AgentBootstrap.class.getProtectionDomain().getCodeSource();
            if (codeSource != null) {
                File agentCoreJarFile = new File(codeSource.getLocation().toURI().getSchemeSpecificPart());
                File spyJarFile = new File(agentCoreJarFile.getParentFile(), AGENT_SPY_JAR);
                instrumentation.appendToBootstrapClassLoaderSearch(new JarFile(spyJarFile));
            } else {
                throw new IllegalStateException("can not find " + AGENT_SPY_JAR);
            }

        }
    }

    private void enhanceClassLoader() throws IOException, UnmodifiableClassException {
        AgentClassLoaderProperties agentClassLoaderProperties = agentSpringBootContainer.getAgentClassLoaderProperties();
        if (agentClassLoaderProperties.getEnhanceLoaders() == null) {
            return;
        }
        Set<String> loaders = new HashSet<>();

        for (String enhanceLoader : agentClassLoaderProperties.getEnhanceLoaders()) {
            loaders.add(enhanceLoader.trim());
        }

        // 增强 ClassLoader#loadClsss ，解决一些ClassLoader加载不到 SpyAPI的问题
        // https://github.com/alibaba/arthas/issues/1596
        byte[] classBytes = IOUtils.getBytes(AgentBootstrap.class.getClassLoader()
                .getResourceAsStream(ClassLoader_Instrument.class.getName().replace('.', '/') + ".class"));

        SimpleClassMatcher matcher = new SimpleClassMatcher(loaders);
        InstrumentConfig instrumentConfig = new InstrumentConfig(AsmUtils.toClassNode(classBytes), matcher);

        InstrumentParseResult instrumentParseResult = new InstrumentParseResult();
        instrumentParseResult.addInstrumentConfig(instrumentConfig);
        classLoaderInstrumentTransformer = new InstrumentTransformer(instrumentParseResult);
        instrumentation.addTransformer(classLoaderInstrumentTransformer, true);

        if (loaders.size() == 1 && loaders.contains(ClassLoader.class.getName())) {

            // 如果只增强 java.lang.ClassLoader，可以减少查找过程
            instrumentation.retransformClasses(ClassLoader.class);

        } else {

            InstrumentationUtils.triggerRetransformClasses(instrumentation, loaders);

        }

    }

    private void initAgentContainer(Map<String, String> argsMap, Instrumentation ins) {
        ConfigurableApplicationContext configurableApplicationContext = AgentSpringBootContainer.main(argsMap, ins);
        log.error("The performance analysis container is started! ClassLoader: {}， Container: {}", configurableApplicationContext.getClassLoader(), configurableApplicationContext.getDisplayName());
        agentSpringBootContainer = configurableApplicationContext.getBean(AgentSpringBootContainer.class);
    }

    /**
     * call reset() before destroy()
     */
    public void destroy() {

        if (transformerManager != null) {
            transformerManager.destroy();
        }
        if (classLoaderInstrumentTransformer != null) {
            instrumentation.removeTransformer(classLoaderInstrumentTransformer);
        }
        // clear the reference in Spy class.
        cleanUpSpyReference();
        if (shutdown != null) {
            try {
                Runtime.getRuntime().removeShutdownHook(shutdown);
            } catch (Throwable t) {
                // ignore
            }
        }

        //log.info("spring-profiling-server destroy completed.");
    }

    /**
     * 清除SpyAPI里的引用
     */
    private void cleanUpSpyReference() {
        try {
            SpyAPI.setNopSpy();
            SpyAPI.destroy();
        } catch (Throwable e) {
            // ignore
        }
        // AgentBootstrap.resetAgentClassLoader();
        try {
            Class<?> clazz = ClassLoader.getSystemClassLoader().loadClass("com.lcsc.wm.agent.agent334.AgentBootstrap");
            Method method = clazz.getDeclaredMethod("resetAgentClassLoader");
            method.invoke(null);
        } catch (Throwable e) {
            // ignore
        }
    }

}
