package com.lcsc.wm.agent.plugin.core.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @description:
 * @author: Designer
 * @date : 2024-08-21 23:36
 */
public class ClassPathScanningCandidateComponentProviderInvoke {

    public static Set<String> scan(ClassLoader classLoader, Collection<String> scanPackages) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        return scan(classLoader, true, Collections.emptyList(), scanPackages);
    }

    public static Set<String> scan(ClassLoader classLoader, boolean useDefaultFilters, Collection<String> annotationClasses, Collection<String> scanPackages) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        Class<?> scannerClass = Class.forName("org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider", true, classLoader);
        Object scanner = scannerClass.getConstructor(boolean.class).newInstance(useDefaultFilters);

        if (!useDefaultFilters) {
            for (String annotationClassName : annotationClasses) {
                Class<? extends Annotation> annotationClass = (Class<? extends Annotation>) Class.forName(annotationClassName, true, classLoader);
                //
                if (annotationClass.isAnnotation()) {
                    //
                    Class<?> typeFilterClass = Class.forName("org.springframework.core.type.filter.TypeFilter", true, classLoader);
                    scannerClass.getMethod("addIncludeFilter", typeFilterClass).invoke(scanner, Class.forName("org.springframework.core.type.filter.AnnotationTypeFilter", true, classLoader).getConstructor(Class.class).newInstance(annotationClass));
                }
            }
        }

        //
        Class<?> resourceLoaderClass = Class.forName("org.springframework.core.io.ResourceLoader", true, classLoader);
        scannerClass.getMethod("setResourceLoader", resourceLoaderClass).invoke(scanner, Class.forName("org.springframework.core.io.DefaultResourceLoader", true, classLoader).getConstructor(ClassLoader.class).newInstance(classLoader));

        Set<String> beanDefinitionNames = new HashSet<>();
        for (String scanPackage : scanPackages) {
            //Set<BeanDefinition>
            Set<Object> beanDefinitions = (Set<Object>) (scannerClass.getMethod("findCandidateComponents", String.class).invoke(scanner, scanPackage));

            for (Object beanDefinition : beanDefinitions) {
                Object beanClassName = beanDefinition.getClass().getMethod("getBeanClassName").invoke(beanDefinition);
                if (beanClassName != null) {
                    beanDefinitionNames.add((String) beanClassName);
                }
            }

        }

        return beanDefinitionNames;
    }

}
