package com.lcsc.wm.agent.framework.source;

import com.lcsc.wm.agent.framework.vo.ByteKitUtils;
import com.lcsc.wm.agent.framework.vo.ClassMethodInfo;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AgentSourceAttribute {

    protected Map<String, ClassMethodInfo> cache = new ConcurrentHashMap<>();

    @Getter
    protected ClassMethodInfo classMethodInfo;

    public AgentSourceAttribute(ClassMethodInfo classMethodInfo) {
        this.classMethodInfo = classMethodInfo;
    }

    public boolean isCandidateClass(String className) {
        return classMethodInfo.isCandidateClass(className);
    }

    public boolean isCandidateMethod(String className, String methodName, String methodDesc) {
        String cacheKey = getCacheKey(className, methodName, methodDesc);
        if (cache.containsKey(cacheKey)) {

            return true;

        } else {

            if (isCandidateMethod0(className, methodName, methodDesc)) {
                cache.put(cacheKey, classMethodInfo);
                return true;
            }

        }

        return false;
    }

    public ClassMethodInfo getSourceAttribute() {
        return classMethodInfo;
    }

    public ClassMethodInfo getSourceAttribute(String className, String methodName, String methodDesc) {
        return cache.get(getCacheKey(className, methodName, methodDesc));
    }

    /**
     * 是否候选方法
     *
     * @param className
     * @param methodName
     * @param methodDesc
     * @return
     */
    protected boolean isCandidateMethod0(String className, String methodName, String methodDesc) {
        return classMethodInfo.isCandidateMethod(methodName, ByteKitUtils.getMethodArgumentTypes(methodDesc));
    }

    protected String getCacheKey(String className, String methodName, String methodDesc) {
        return className + "#" + methodName + methodDesc;
    }

}
