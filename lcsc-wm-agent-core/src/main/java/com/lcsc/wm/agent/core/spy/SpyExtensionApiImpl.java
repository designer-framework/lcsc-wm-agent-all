package com.lcsc.wm.agent.core.spy;

import com.lcsc.wm.agent.framework.advice.Advice;
import com.lcsc.wm.agent.framework.advisor.PointcutAdvisor;
import com.lcsc.wm.agent.framework.enums.InvokeType;
import com.lcsc.wm.agent.framework.pointcut.Pointcut;
import com.lcsc.wm.agent.framework.spy.SpyExtensionApi;
import com.lcsc.wm.agent.framework.vo.ByteKitUtils;
import com.lcsc.wm.agent.framework.vo.InvokeVO;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <pre>
 * 怎么从 className|methodDesc 到 id 对应起来？？
 * 当id少时，可以id自己来判断是否符合？
 *
 * 如果是每个 className|methodDesc 为 key ，是否
 * </pre>
 *
 * @author hengyunabc 2020-04-24
 */
@Slf4j
public class SpyExtensionApiImpl implements SpyExtensionApi {

    /**
     * 调用链
     */
    private final ThreadLocal<Deque<Long>> stack = ThreadLocal.withInitial(LinkedList::new);

    /**
     * 调用链
     */
    private final AtomicLong ID_GENERATOR = new AtomicLong(1000);

    private final List<PointcutAdvisor> pointcutAdvisors;

    public SpyExtensionApiImpl(List<PointcutAdvisor> pointcutAdvisors) {
        this.pointcutAdvisors = pointcutAdvisors;
    }

    @Override
    public void atEnter(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args, Map<String, Object> attach) {
        long currInvokeId = ID_GENERATOR.addAndGet(1);
        stack.get().push(currInvokeId);

        proceed(
                clazz, methodName, methodDesc, InvokeType.ENTER
                , advice ->
                        advice.before(InvokeVO.newForBefore(
                                clazz.getClassLoader(), clazz, methodName, ByteKitUtils.getMethodArgumentTypes(methodDesc), target, args, InvokeType.ENTER
                                , currInvokeId, attach
                        ))
        );
    }

    @Override
    public void atExit(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args, Object returnObject, Map<String, Object> attach) {
        Long currInvokeId = stack.get().pop();

        proceed(
                clazz, methodName, methodDesc, InvokeType.EXIT
                , advice ->
                        advice.afterReturning(InvokeVO.newForAfterReturning(
                                clazz.getClassLoader(), clazz, methodName, ByteKitUtils.getMethodArgumentTypes(methodDesc), target, args, returnObject, InvokeType.EXIT
                                , currInvokeId, attach
                        ))
        );
    }

    @Override
    public void atExceptionExit(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args, Throwable throwable, Map<String, Object> attach) {

        if(stack.get().isEmpty()){
            return;
        }

        Long currInvokeId = stack.get().pop();

        proceed(
                clazz, methodName, methodDesc, InvokeType.EXCEPTION
                , advice ->
                        advice.afterThrowing(InvokeVO.newForAfterThrowing(
                                clazz.getClassLoader(), clazz, methodName, ByteKitUtils.getMethodArgumentTypes(methodDesc), target, args, throwable, InvokeType.EXCEPTION
                                , currInvokeId, attach
                        ))
        );
    }

    /**
     * @param clazz
     * @param methodName
     * @param methodDesc
     * @param invokeType
     * @param invokeConsumer
     */
    private void proceed(Class<?> clazz, String methodName, String methodDesc, InvokeType invokeType, Consumer<Advice> invokeConsumer) {
        try {

            //遍历切入点Advisor
            for (PointcutAdvisor pointcutAdvisor : pointcutAdvisors) {

                //判断是否候选者
                Pointcut pointcut = pointcutAdvisor.getPointcut();
                if (pointcut.isHit(clazz.getName(), methodName, methodDesc)) {

                    //切点调用
                    Advice advice = pointcutAdvisor.getAdvice();
                    invokeConsumer.accept(advice);

                }

            }

        } catch (Throwable e) {
            log.error("{} -> 异常, Class:{}, Method: {}", invokeType, clazz.getName(), methodName, e);
        }
    }

    interface Consumer<T> {
        void accept(T o) throws Throwable;
    }

}
