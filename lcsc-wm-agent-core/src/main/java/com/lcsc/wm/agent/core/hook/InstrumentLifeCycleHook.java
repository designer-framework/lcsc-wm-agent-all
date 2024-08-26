package com.lcsc.wm.agent.core.hook;

import com.alibaba.bytekit.agent.inst.Instrument;
import com.alibaba.bytekit.asm.instrument.InstrumentParseResult;
import com.alibaba.bytekit.asm.instrument.InstrumentTemplate;
import com.alibaba.bytekit.asm.instrument.InstrumentTransformer;
import com.alibaba.bytekit.utils.IOUtils;
import com.lcsc.wm.agent.core.configuration.instrument.RetransformAttribute;
import com.lcsc.wm.agent.core.constants.LifeCycleOrdered;
import com.lcsc.wm.agent.core.lifecycle.AgentLifeCycleHook;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.core.PriorityOrdered;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class InstrumentLifeCycleHook implements AgentLifeCycleHook, BeanClassLoaderAware, PriorityOrdered {

    private final List<RetransformAttribute> retransformAttributes;

    @Setter
    private ClassLoader beanClassLoader;

    @Setter
    private Instrumentation instrumentation;

    public InstrumentLifeCycleHook(List<RetransformAttribute> retransformAttributes) {
        this.retransformAttributes = retransformAttributes;
    }

    @Override
    public void start() {

        InstrumentTemplate template = new InstrumentTemplate();

        try {

            List<String> retransformClasses = new ArrayList<>();
            for (RetransformAttribute retransformAttribute : retransformAttributes) {
                try {

                    Class<?> instrumentClass = retransformAttribute.getInstrumentClass();
                    Instrument instrument = instrumentClass.getAnnotation(Instrument.class);

                    if (instrument != null && instrument.Class().length > 0) {

                        byte[] classBytes = IOUtils.getBytes(
                                beanClassLoader.getResourceAsStream(retransformAttribute.getInstrumentClass().getName().replace('.', '/') + ".class")
                        );
                        template.addInstrumentClass(classBytes);
                        retransformClasses.add(instrument.Class()[0]);

                    } else {
                        log.error("Enhancement failed: Class [{}] No @Instrument annotation", retransformAttribute.getInstrumentClass());
                    }

                } catch (Exception e) {
                    log.error("Enhancement failed: {}", retransformAttribute.getInstrumentClass());
                }
            }

            InstrumentParseResult instrumentParseResult = template.build();
            InstrumentTransformer instrumentTransformer = null;
            try {

                instrumentTransformer = new InstrumentTransformer(instrumentParseResult);
                instrumentation.addTransformer(instrumentTransformer, true);

                //此时的类加载器是AppClassLoader或SpringBoot自带的类加载器
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                for (String retransformClass : retransformClasses) {
                    log.info("RetransformClass success: {}", retransformClass);
                    instrumentation.retransformClasses(cl.loadClass(retransformClass));
                }

            } finally {

                instrumentation.removeTransformer(instrumentTransformer);

            }

        } catch (Exception e) {
            log.error("Enhancement failed", e);
        }

    }

    @Override
    public int getOrder() {
        return LifeCycleOrdered.AGENT_RETRANSFORM;
    }

}
