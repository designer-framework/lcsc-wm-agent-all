package com.lcsc.wm.agent.plugin.core.turbo.advisor;

import com.alibaba.bytekit.utils.ReflectionUtils;
import com.lcsc.wm.agent.core.vo.MethodInvokeVO;
import com.lcsc.wm.agent.framework.vo.ClassMethodInfo;
import com.lcsc.wm.agent.framework.vo.InvokeVO;
import com.lcsc.wm.agent.plugin.core.analysis.component.ApolloCreatorPointcutAdvisor;
import com.lcsc.wm.agent.plugin.core.enums.ComponentEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.ConfigurableEnvironment;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * Apollo加载配置耗时统计
 *
 * @description:
 * @author: Designer
 * @date : 2024-08-01 01:10
 * @see com.ctrip.framework.apollo.build.ApolloInjector#getInstance(java.lang.Class)
 */
@Slf4j
public class ApolloCreatorTurboPointcutAdvisor extends ApolloCreatorPointcutAdvisor {

    /**
     * @see com.ctrip.framework.apollo.spring.config.PropertySourcesConstants#APOLLO_BOOTSTRAP_NAMESPACES
     */
    private static final String APOLLO_BOOTSTRAP_NAMESPACES = "apollo.bootstrap.namespaces";

    /**
     * @return
     * @see com.ctrip.framework.apollo.spring.boot.ApolloApplicationContextInitializer#initialize(ConfigurableEnvironment)
     */
    public ApolloCreatorTurboPointcutAdvisor(
            ComponentEnum componentEnum, ClassMethodInfo classMethodInfo
    ) {
        super(componentEnum, classMethodInfo);
    }

    /**
     * @param invokeVO
     * @param methodInvokeVO
     * @see com.ctrip.framework.apollo.spring.boot.ApolloApplicationContextInitializer#initialize(ConfigurableEnvironment)
     */
    @Override
    public void atMethodInvokeBefore(InvokeVO invokeVO, MethodInvokeVO methodInvokeVO) {
        super.atMethodInvokeBefore(invokeVO, methodInvokeVO);
        /**
         * @see ConfigurableEnvironment#getProperty(String)
         */
        //获取Apollo配置
        Object configurableEnvironment = invokeVO.getParams()[0];
        Method getProperty = ReflectionUtils.findMethod(configurableEnvironment.getClass(), "getProperty", String.class);

        //分割命名空间
        Object namespaces = ReflectionUtils.invokeMethod(getProperty, configurableEnvironment, APOLLO_BOOTSTRAP_NAMESPACES);
        List<String> namespacesList = Arrays
                .stream(String.valueOf(namespaces).split(","))
                .map(String::trim)
                .collect(Collectors.toCollection(LinkedList::new));

        log.info("Apollo namespace: {}", namespacesList);

        //多线程加载命名空间
        CountDownLatch countDownLatch = new CountDownLatch(namespacesList.size());

        //异步加载命名空间
        namespacesList.forEach(namespace -> {

            CompletableFuture.runAsync(() -> {

                try {

                    log.info("Load Apollo Namespace: {}", namespace);
                    /**
                     * @see ConfigService#getConfig(String)
                     */
                    Method getConfig = Class.forName("com.ctrip.framework.apollo.ConfigService", false, invokeVO.getLoader())
                            .getMethod("getConfig", String.class);
                    ReflectionUtils.invokeMethod(getConfig, null, namespace);
                    log.info("Apollo namespace load success: {}", namespace);

                } catch (Exception e) {

                    log.error("Apollo namespace load error: {}", namespace, e);

                } finally {

                    countDownLatch.countDown();

                }

            });

        });

        try {
            //全部加载完毕
            countDownLatch.await();
        } catch (Exception e) {
            throw new IllegalStateException("Apollo namespace async load failed", e);
        }

    }

}
