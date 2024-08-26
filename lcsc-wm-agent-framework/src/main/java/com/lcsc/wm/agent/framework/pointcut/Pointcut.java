package com.lcsc.wm.agent.framework.pointcut;

import com.lcsc.wm.agent.framework.interceptor.SpyInterceptorApi;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-23 22:30
 */
public interface Pointcut {

    Pointcut FALSE = new Pointcut() {
        @Override
        public boolean getCanRetransform() {
            return false;
        }

        @Override
        public boolean isCandidateClass(String className) {
            return false;
        }

        @Override
        public boolean isCandidateMethod(String className, String methodName, String methodDesc) {
            return false;
        }

        @Override
        public boolean isHit(String className, String methodName, String methodDesc) {
            return false;
        }

        @Override
        public Class<? extends SpyInterceptorApi> getInterceptor() {
            return SpyInterceptorApi.class;
        }

    };

    /**
     * 是否允许重新装载
     *
     * @return
     */
    boolean getCanRetransform();

    /**
     * 是否候选类
     *
     * @param className
     * @return
     */
    boolean isCandidateClass(String className);

    /**
     * 是否候选方法
     *
     * @param className
     * @param methodName
     * @param methodDesc
     * @return
     */
    boolean isCandidateMethod(String className, String methodName, String methodDesc);

    /**
     * 插桩方法被调用时先从缓存中读取, 读取不到则说明这个插桩方法不是当前切点的候选者, 直接匹配下一个切点
     *
     * @param className
     * @param methodName
     * @param methodDesc
     * @return
     */
    boolean isHit(String className, String methodName, String methodDesc);

    /**
     * @return
     * @see com.lcsc.wm.agent.core.interceptor.SimpleSpyInterceptorApi
     */
    Class<? extends SpyInterceptorApi> getInterceptor();

}
