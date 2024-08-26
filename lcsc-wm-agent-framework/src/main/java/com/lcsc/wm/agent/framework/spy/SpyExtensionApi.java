package com.lcsc.wm.agent.framework.spy;

import java.util.Map;

/**
 * 便于外部拓展
 */
public interface SpyExtensionApi {

    void atEnter(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args, Map<String, Object> attach);

    void atExit(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args, Object returnObject, Map<String, Object> attach);

    void atExceptionExit(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args, Throwable throwable, Map<String, Object> attach);

}
