package com.lcsc.wm.agent.core.utils;

import com.lcsc.wm.agent.framework.pointcut.Pointcut;
import lombok.extern.slf4j.Slf4j;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author hengyunabc 2020-05-25
 */
@Slf4j
public class InstrumentationUtils {

    public static void retransformClasses(Instrumentation inst, ClassFileTransformer transformer,
                                          Set<Class<?>> classes) {
        try {
            inst.addTransformer(transformer, true);

            for (Class<?> clazz : classes) {
                if (isLambdaClass(clazz)) {
                    log.info(
                            "ignore lambda class: {}, because jdk do not support retransform lambda class: https://github.com/alibaba/arthas/issues/1512.",
                            clazz.getName());
                    continue;
                }
                try {
                    inst.retransformClasses(clazz);
                } catch (Throwable e) {
                    String errorMsg = "retransformClasses class error, name: " + clazz.getName();
                    log.error(errorMsg, e);
                }
            }
        } finally {
            inst.removeTransformer(transformer);
        }
    }

    public static void triggerRetransformClasses(Instrumentation inst, List<Pointcut> pointcuts) {

        for (Class<?> clazz : inst.getAllLoadedClasses()) {
            //

            for (Pointcut pointcut : pointcuts) {

                if (pointcut.isCandidateClass(clazz.getName())) {

                    try {
                        inst.retransformClasses(clazz);
                    } catch (Throwable e) {
                        String errorMsg = "retransformClasses class error, name: " + clazz.getName();
                        log.error(errorMsg, e);
                    }

                }

            }

        }
    }

    public static void triggerRetransformClasses(Instrumentation inst, Collection<String> classes) {
        for (Class<?> clazz : inst.getAllLoadedClasses()) {
            if (classes.contains(clazz.getName())) {
                try {
                    inst.retransformClasses(clazz);
                } catch (Throwable e) {
                    String errorMsg = "retransformClasses class error, name: " + clazz.getName();
                    log.error(errorMsg, e);
                }
            }
        }
    }

    public static boolean isLambdaClass(Class<?> clazz) {
        return (clazz.isSynthetic() && (clazz.getSuperclass() == Object.class) &&
                (clazz.getInterfaces().length > 0) && clazz.getName().contains("$$Lambda"));
    }

}
