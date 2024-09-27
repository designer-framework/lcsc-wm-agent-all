package com.lcsc.wm.agent.plugin.core.analysis.bean;

import com.lcsc.wm.agent.core.advisor.SimpleMethodInvokePointcutAdvisor;
import com.lcsc.wm.agent.core.constants.LifeCycleOrdered;
import com.lcsc.wm.agent.core.lifecycle.AgentLifeCycleHook;
import com.lcsc.wm.agent.core.vo.MethodInvokeVO;
import com.lcsc.wm.agent.framework.vo.ClassMethodInfo;
import com.lcsc.wm.agent.framework.vo.InvokeVO;
import com.lcsc.wm.agent.plugin.core.constants.SpringBeanTag;
import com.lcsc.wm.agent.plugin.core.enums.SpringBeanLifeCycleEnum;
import com.lcsc.wm.agent.plugin.core.events.*;
import com.lcsc.wm.agent.plugin.core.vo.SpringAgentStatistics;
import com.lcsc.wm.agent.plugin.core.vo.SpringBeanVO;
import com.lcsc.wm.agent.plugin.core.vo.SpringLabelEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationListener;
import org.springframework.core.NamedThreadLocal;

import java.util.Stack;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class SpringBeanCreationPointcutAdvisor extends SimpleMethodInvokePointcutAdvisor implements ApplicationListener<BeanCreationLifeCycleEvent>, DisposableBean, InitializingBean, AgentLifeCycleHook, CreatingBean {

    private final AtomicLong ID_GENERATOR = new AtomicLong(1000);

    /**
     * 调用链
     */
    private final ThreadLocal<Stack<SpringBeanVO>> beanCreateStack = NamedThreadLocal.withInitial(Stack::new);

    private final SpringAgentStatistics springAgentStatistics;

    public SpringBeanCreationPointcutAdvisor(ClassMethodInfo classMethodInfo, SpringAgentStatistics springAgentStatistics) {
        super(classMethodInfo);
        this.springAgentStatistics = springAgentStatistics;
    }

    /**
     * 创建Bean, 入栈
     *
     * @param invokeVO
     */
    @Override
    protected void atBefore(InvokeVO invokeVO) {
        //先生成id
        SpringBeanVO creatingBean = new SpringBeanVO(ID_GENERATOR.incrementAndGet(), String.valueOf(invokeVO.getParams()[0]));
        //
        Stack<SpringBeanVO> beanCreateStack = this.beanCreateStack.get();
        //子Bean
        if (!beanCreateStack.isEmpty()) {

            SpringBeanVO parentSpringBeanVO = beanCreateStack.peek();
            parentSpringBeanVO.addDependBean(creatingBean);

            //入栈
            beanCreateStack.push(creatingBean);

            //父Bean
        } else {

            //入栈
            beanCreateStack.push(creatingBean);

        }

        super.atBefore(invokeVO);

        //采集已创建的Bean
        springAgentStatistics.addCreatedBean(creatingBean);
    }

    /**
     * 创建Bean成功, 出栈
     */
    @Override
    protected void atMethodInvokeAfter(InvokeVO invokeVO, MethodInvokeVO methodInvokeVO) {
        super.atMethodInvokeAfter(invokeVO, methodInvokeVO);
        //bean初始化结束, 出栈

        SpringBeanVO springBeanVO = beanCreateStack.get().pop();
        springBeanVO.initialized();

        //完善已创建Bean的一些基本信息
        addBeanTags(invokeVO, springBeanVO)
                //计算Bean创建耗时
                .calcBeanLoadTime();
    }

    @Override
    protected String getInvokeKey(InvokeVO invokeVO) {
        return super.getInvokeKey(invokeVO) + ":" + beanCreateStack.get().peek().getId();
    }

    private SpringBeanVO addBeanTags(InvokeVO invokeVO, SpringBeanVO creatingBean) {
        creatingBean.addTag(SpringBeanTag.threadName, Thread.currentThread().getName());
        creatingBean.addTag(SpringBeanTag.classLoader, getBeanClassLoader(invokeVO.getReturnObj()));
        creatingBean.addTag(SpringBeanTag.className, invokeVO.getReturnObj() == null ? null : invokeVO.getReturnObj().getClass().getName());
        return creatingBean;
    }

    private String getBeanClassLoader(Object returnBean) {
        if (returnBean != null) {

            ClassLoader classLoader = returnBean.getClass().getClassLoader();
            if (classLoader != null) {
                return classLoader.getClass().getName();
            } else {
                return "Bootstrap";
            }

        } else {

            return "Bootstrap";

        }
    }

    /**
     * AOP,InstantiateSingleton 等类型的耗时推送
     *
     * @param beanCreationLifeCycleEvent the event to respond to
     */
    @Override
    public void onApplicationEvent(BeanCreationLifeCycleEvent beanCreationLifeCycleEvent) {
        if (beanCreationLifeCycleEvent instanceof BeanAopProxyCreatedLifeCycleEvent) {

            BeanAopProxyCreatedLifeCycleEvent beanAopProxyCreatedEvent = (BeanAopProxyCreatedLifeCycleEvent) beanCreationLifeCycleEvent;

            springAgentStatistics.fillBeanCreate(getCreatingBeanName(), beanCreateVO -> {
                beanCreateVO.addBeanLifeCycle(SpringBeanLifeCycleEnum.CreateAopProxyClass, beanAopProxyCreatedEvent.getLifeCycleDurations());
            });

        } else if (beanCreationLifeCycleEvent instanceof PostConstructMethodInvokeLifeCycleEvent) {

            PostConstructMethodInvokeLifeCycleEvent beanInitMethodInvokeEvent = (PostConstructMethodInvokeLifeCycleEvent) beanCreationLifeCycleEvent;

            springAgentStatistics.fillBeanCreate(getCreatingBeanName(), beanCreateVO -> {
                beanCreateVO.addBeanLifeCycle(SpringBeanLifeCycleEnum.PostConstruct, beanInitMethodInvokeEvent.getLifeCycleDurations());
            });

        } else if (beanCreationLifeCycleEvent instanceof InitializingBeanMethodInvokeLifeCycleEvent) {

            InitializingBeanMethodInvokeLifeCycleEvent initializingBeanMethodInvokeLifeCycleEvent = (InitializingBeanMethodInvokeLifeCycleEvent) beanCreationLifeCycleEvent;

            springAgentStatistics.fillBeanCreate(getCreatingBeanName(), beanCreateVO -> {
                beanCreateVO.addBeanLifeCycle(SpringBeanLifeCycleEnum.AfterPropertiesSet, initializingBeanMethodInvokeLifeCycleEvent.getLifeCycleDurations());
            });

        } else if (beanCreationLifeCycleEvent instanceof SmartInstantiateSingletonLifeCycleEvent) {

            SmartInstantiateSingletonLifeCycleEvent smartInstantiateSingletonEvent = (SmartInstantiateSingletonLifeCycleEvent) beanCreationLifeCycleEvent;

            springAgentStatistics.fillBeanCreate(smartInstantiateSingletonEvent.getBeanName(), beanCreateVO -> {
                beanCreateVO.addBeanLifeCycle(SpringBeanLifeCycleEnum.AfterSingletonsInstantiated, smartInstantiateSingletonEvent.getLifeCycleDurations());
            });

        } else {

            log.warn("Source: {}, BeanName: {}", beanCreationLifeCycleEvent.getSource(), beanCreationLifeCycleEvent.getBeanName());

        }
    }

    @Override
    public void stop() {
        applicationEventPublisher.publishEvent(
                SpringBootApplicationLabelEvent.create(this, SpringLabelEnum.BEAN_NUM, springAgentStatistics.getCreatedBeans().size())
        );
    }

    @Override
    public void destroy() {
        beanCreateStack.remove();
    }

    @Override
    public int getOrder() {
        return LifeCycleOrdered.SPRING_APPLICATION_INFO;
    }

    @Override
    public String getCreatingBeanName() {
        SpringBeanVO creatingBeanName = beanCreateStack.get().peek();
        if (creatingBeanName != null) {
            return creatingBeanName.getName();
        } else {
            throw new IllegalStateException("Does not exist creating bean");
        }
    }

}
