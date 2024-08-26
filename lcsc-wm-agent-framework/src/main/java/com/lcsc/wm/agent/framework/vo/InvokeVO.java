package com.lcsc.wm.agent.framework.vo;

import com.lcsc.wm.agent.framework.enums.InvokeType;
import lombok.Getter;

import java.util.Map;

/**
 * 通知点 Created by vlinux on 15/5/20.
 */
@Getter
public class InvokeVO {

    private final ClassLoader loader;
    private final Class<?> clazz;
    private final Object target;
    private final Object[] params;
    private final Object returnObj;
    private final Throwable throwExp;
    private final InvokeType invokeType;

    //将ArthasMethod的字段直接放到当前类
    private final String methodName;

    private final String[] methodArgumentTypes;

    private final long invokeId;

    private final Map<String, Object> attach;

    /**
     * for finish
     *
     * @param loader              类加载器
     * @param clazz               类
     * @param methodName          方法名
     * @param methodArgumentTypes 方法入参
     * @param target              目标类
     * @param params              调用参数
     * @param returnObj           返回值
     * @param invokeType          调用节点
     * @param invokeId            调用ID
     * @param throwExp            抛出异常
     * @param attach              进入场景
     */
    protected InvokeVO(
            ClassLoader loader,
            Class<?> clazz,
            String methodName, String[] methodArgumentTypes,
            Object target,
            Object[] params,
            Object returnObj,
            Throwable throwExp,
            InvokeType invokeType,
            long invokeId, Map<String, Object> attach
    ) {
        this.loader = loader;
        this.clazz = clazz;
        this.methodName = methodName;
        this.methodArgumentTypes = methodArgumentTypes;
        this.target = target;
        this.params = params;
        this.returnObj = returnObj;
        this.throwExp = throwExp;
        this.invokeType = invokeType;
        this.invokeId = invokeId;
        this.attach = attach;
    }

    public static InvokeVO newForBefore(
            ClassLoader loader, Class<?> clazz, String methodName, String[] methodArgumentTypes, Object target, Object[] params, InvokeType invokeType
            , long invokeId, Map<String, Object> attach
    ) {
        return new InvokeVO(loader, clazz, methodName, methodArgumentTypes, target, params, null, //returnObj
                null, //throwExp
                invokeType, invokeId, attach);
    }

    public static InvokeVO newForAfterReturning(
            ClassLoader loader, Class<?> clazz, String methodName, String[] methodArgumentTypes, Object target, Object[] params, Object returnObj, InvokeType invokeType
            , long invokeId, Map<String, Object> attach
    ) {
        return new InvokeVO(loader, clazz, methodName, methodArgumentTypes, target, params, returnObj, null, //throwExp
                invokeType, invokeId, attach);
    }

    public static InvokeVO newForAfterThrowing(
            ClassLoader loader, Class<?> clazz, String methodName, String[] methodArgumentTypes, Object target, Object[] params, Throwable throwExp, InvokeType invokeType
            , long invokeId, Map<String, Object> attach
    ) {
        return new InvokeVO(loader, clazz, methodName, methodArgumentTypes, target, params, null, //returnObj
                throwExp, invokeType, invokeId, attach);
    }

    @Override
    public String toString() {
        return invokeId + " | " + invokeType + " | " + clazz.getName() + "#" + methodName + "(" + String.join(",", methodArgumentTypes) + ")";
    }

}
