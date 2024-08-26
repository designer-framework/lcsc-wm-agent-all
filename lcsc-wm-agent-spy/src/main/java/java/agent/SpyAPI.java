package java.agent;

import java.util.Map;

/**
 * <pre>
 * 一个adviceId 是什么呢？ 就是一个trace/monitor/watch命令能对应上的一个id，比如一个类某个函数，它的 enter/end/exception 统一是一个id，分配完了就不会再分配。
 *
 * 同样一个method，如果它trace之后，也会有一个 adviceId， 这个method里的所有invoke都是统一处理，认为是一个 adviceId 。 但如果有匹配到不同的 invoke的怎么分配？？
 * 好像有点难了。。
 *
 * 其实就是把所有可以插入的地方都分类好，那么怎么分类呢？？ 或者是叫同一种匹配，就是同一种的 adviceId?
 *
 * 比如入参是有  class , method ,是固定的  ,  某个行号，或者 某个
 *
 * aop插入的叫 adviceId ， command插入的叫 ListenerId？
 *
 *
 *
 * </pre>
 *
 * @author hengyunabc
 */
public class SpyAPI {

    public static final AbstractSpy NOPSPY = new NopSpy();

    public static volatile boolean INITED;

    private static volatile AbstractSpy spyInstance = NOPSPY;

    public static AbstractSpy getSpy() {
        return spyInstance;
    }

    public static void setSpy(AbstractSpy spy) {
        spyInstance = spy;
    }

    public static void setNopSpy() {
        setSpy(NOPSPY);
    }

    public static boolean isNopSpy() {
        return NOPSPY == spyInstance;
    }

    public static void init() {
        INITED = true;
    }

    public static boolean isInited() {
        return INITED;
    }

    public static void destroy() {
        setNopSpy();
        INITED = false;
    }

    public static void atEnter(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args, Map<String, Object> attach) {
        spyInstance.atEnter(clazz, methodName, methodDesc, target, args, attach);
    }

    public static void atExit(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args, Object returnObject, Map<String, Object> attach) {
        spyInstance.atExit(clazz, methodName, methodDesc, target, args, returnObject, attach);
    }

    public static void atExceptionExit(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args, Throwable throwable, Map<String, Object> attach) {
        spyInstance.atExceptionExit(clazz, methodName, methodDesc, target, args, throwable, attach);
    }

    public static abstract class AbstractSpy {

        public void atEnter(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args, Map<String, Object> attach) {
        }

        public void atExit(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args, Object returnObject, Map<String, Object> attach) {
        }

        public void atExceptionExit(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args, Throwable throwable, Map<String, Object> attach) {
        }

    }

    static class NopSpy extends AbstractSpy {

    }

}
