package com.lcsc.wm.agent.core.transformer;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * <pre>
 * * 统一管理 ClassFileTransformer
 * * 每个增强命令对应一个 Enhancer ，也统一在这里管理
 * </pre>
 *
 * @author hengyunabc 2020-05-18
 */
public class TransformerManager {

    private Instrumentation instrumentation;

    /**
     * 先于 watch/trace的 Transformer TODO 改进为全部用 order 排序？
     */
    private List<ClassFileTransformer> reTransformers = new CopyOnWriteArrayList<>();

    private ClassFileTransformer classFileTransformer;

    public TransformerManager(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;

        classFileTransformer = new ClassFileTransformer() {

            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                    ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                for (ClassFileTransformer classFileTransformer : reTransformers) {
                    byte[] transformResult = classFileTransformer.transform(loader, className, classBeingRedefined,
                            protectionDomain, classfileBuffer);
                    if (transformResult != null) {
                        classfileBuffer = transformResult;
                    }
                }

                return classfileBuffer;
            }

        };
        instrumentation.addTransformer(classFileTransformer, true);
    }

    public void addRetransformer(ClassFileTransformer transformer) {
        reTransformers.add(transformer);
    }

    public void removeTransformer(ClassFileTransformer transformer) {
        reTransformers.remove(transformer);
    }

    public void destroy() {
        reTransformers.clear();
        instrumentation.removeTransformer(classFileTransformer);
    }

}
