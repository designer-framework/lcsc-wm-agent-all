package com.lcsc.wm.agent.framework.vo;

import com.alibaba.deps.org.objectweb.asm.Type;

public class ByteKitUtils {

    /**
     * 方法签名解析成易于阅读的字段
     *
     * @param methodDesc
     * @return
     */
    public static String[] getMethodArgumentTypes(String methodDesc) {
        Type methodType = Type.getMethodType(methodDesc);
        Type[] argumentTypes = methodType.getArgumentTypes();
        //方法入参对应的JAVA类型
        String[] javaArgumentTypes = new String[argumentTypes.length];

        for (int i = 0; i < argumentTypes.length; i++) {
            javaArgumentTypes[i] = argumentTypes[i].getClassName();
        }

        return javaArgumentTypes;
    }

}
