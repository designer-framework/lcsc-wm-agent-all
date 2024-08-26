package com.lcsc.wm.agent.plugin.core.constants;

/**
 * 1.
 * -> 加载创建Bean自身的总耗时: actualDuration
 * ---> 生成代理Bean的耗时: createProxyDuration
 * 2.
 * -> 加载SmartInitializingBean耗时: smartInitializingDuration
 * 创建Bean的线程名(不出意外是main): threadName
 * 最终Bean的类名(如被aop代理, 则是代理类名): className
 * 创建Bean的类加载器: classLoader
 */
public final class SpringBeanTag {

    public static final String threadName = "threadName";

    public static final String classLoader = "classLoader";

    public static final String className = "className";

}
