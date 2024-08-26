package com.lcsc.wm.agent.plugin.extisons.configuration;

import com.alibaba.bytekit.agent.inst.Instrument;
import com.lcsc.wm.agent.core.annotation.EnabledInstrument;
import com.lcsc.wm.agent.core.annotation.Retransform;
import org.springframework.core.env.Environment;

import java.io.PrintStream;

/**
 * 用注解实现对目标类方法的替换
 */
@EnabledInstrument({
        //通过修改字节码, 阻止打印Banner
        @Retransform(ClassReTransformPluginExtensionAutoConfiguration.SpringBootBanner.class)
})
public class ClassReTransformPluginExtensionAutoConfiguration {

    @Instrument(Class = "org.springframework.boot.SpringBootBanner")
    static class SpringBootBanner {

        public void printBanner(Environment environment, Class<?> sourceClass, PrintStream printStream) {
            System.out.println("*****************************");
            System.out.println("通过SpringAgent禁用了Banner打印");
            System.out.println("*****************************");
        }

    }

}
