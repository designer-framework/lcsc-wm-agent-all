package com.lcsc.wm.agent.plugin.core.analysis.component;

import com.alibaba.fastjson.JSON;
import com.lcsc.wm.agent.core.constants.LifeCycleOrdered;
import com.lcsc.wm.agent.core.lifecycle.AgentLifeCycleHook;
import com.lcsc.wm.agent.plugin.core.enums.ComponentEnum;
import com.lcsc.wm.agent.plugin.core.events.ComponentChildInitializedEvent;
import com.lcsc.wm.agent.plugin.core.events.ComponentEvent;
import com.lcsc.wm.agent.plugin.core.events.ComponentRootInitializedEvent;
import com.lcsc.wm.agent.plugin.core.vo.InitializedComponent;
import com.lcsc.wm.agent.plugin.core.vo.SpringAgentStatistics;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class ComponentInitializedListener implements ApplicationListener<ComponentEvent>, AgentLifeCycleHook {

    private final SpringAgentStatistics springAgentStatistics;

    /**
     *
     */
    private final ConcurrentLinkedDeque<ComponentEvent> componentEvents = new ConcurrentLinkedDeque<>();

    private final Map<ComponentEnum, InitializedComponent> componentsRoot = new ConcurrentHashMap<>();

    private final Object childComponentLock = new Object();

    private final Map<ComponentEnum, List<InitializedComponent>> childrenWaitRootReady = new ConcurrentHashMap<>();

    private final Object eventQueue = new Object();

    private final CountDownLatch eventReadSuccess = new CountDownLatch(1);

    private final CountDownLatch eventProcessed = new CountDownLatch(1);

    public Thread eventProcessor;

    public Thread processComponentChildren;

    private volatile boolean completion;

    public ComponentInitializedListener(SpringAgentStatistics springAgentStatistics) {
        this.springAgentStatistics = springAgentStatistics;
    }

    /**
     * @param componentEvent the event to respond to
     */
    @Override
    public void onApplicationEvent(ComponentEvent componentEvent) {
        componentEvents.addLast(componentEvent);
        //新事件
        synchronized (eventQueue) {
            eventQueue.notify();
        }
    }

    /**
     * 将子组件和父组件的耗时统计进行解耦
     */
    @Override
    public void start() {
        //
        eventProcessor = new Thread(() -> {

            try {
                while (true) {
                    //没有新的事件, 则等待. 当有新事件进来时, 会被唤醒
                    if (componentEvents.isEmpty()) {

                        //项目启动完成, 把剩下的事件处理完毕即可
                        if (completion) {

                            handleEvent();
                            eventReadSuccess.countDown();
                            break;

                            //睡眠, 等待新事件来触发唤醒
                        } else {

                            synchronized (eventQueue) {
                                eventQueue.wait();
                            }

                        }

                        //处理新事件
                    } else {

                        handleEvent();

                    }
                }
            } catch (Exception e) {
                log.error("EventProcessor thread has an error, please check", e);
            }

        }, "EventProcessor");
        eventProcessor.start();

        //
        processComponentChildren = new Thread(() -> {

            try {
                while (true) {

                    //新组件被创建时被唤醒
                    synchronized (childComponentLock) {
                        childComponentLock.wait();
                    }
                    //
                    synchronized (componentsRoot) {
                        synchronized (childrenWaitRootReady) {
                            fillComponentRootData();
                        }
                    }

                    //项目启动完成
                    if (completion) {

                        eventReadSuccess.await();

                        fillComponentRootData();

                        if (!childrenWaitRootReady.isEmpty()) {
                            synchronized (childComponentLock) {
                                //项目启动完成5秒后数据还没采集完成则报错
                                childComponentLock.wait(5000);
                                fillComponentRootData();
                                if (!childrenWaitRootReady.isEmpty()) {
                                    log.error("ProcessComponentChildren thread has an error, please check: {}", JSON.toJSONString(childrenWaitRootReady));
                                }
                            }
                        }

                        break;
                    }

                }

            } catch (InterruptedException e) {
                log.error("ProcessComponentChildren thread has an error, please check", e);
            } finally {
                eventProcessed.countDown();
            }

        }, "ProcessComponentChildren");
        processComponentChildren.start();
    }

    private void fillComponentRootData() {
        childrenWaitRootReady.forEach((componentEvent, children) -> {

            //父组件已准备好
            InitializedComponent initializedComponent = componentsRoot.computeIfPresent(componentEvent, (key, value) -> {
                value.insertChildren(children);
                return value;
            });

            if (initializedComponent != null) {
                childrenWaitRootReady.remove(componentEvent);
            }

        });
    }

    private void handleEvent() {

        //取事件
        ComponentEvent componentEvent;
        while ((componentEvent = componentEvents.pollFirst()) != null) {

            //新组件被创建
            if (componentEvent instanceof ComponentRootInitializedEvent) {

                synchronized (componentsRoot) {
                    InitializedComponent initializedComponent = ((ComponentRootInitializedEvent) componentEvent).getInitializedComponent();

                    if (!componentsRoot.containsKey(initializedComponent.getComponentName())) {
                        componentsRoot.put(initializedComponent.getComponentName(), initializedComponent);
                    } else {
                        log.error("Duplicate root component: {}", initializedComponent.getComponentName());
                        InitializedComponent existInitializedComponent = componentsRoot.get(initializedComponent.getComponentName());
                        existInitializedComponent.insertChildren(initializedComponent.getChildren());
                    }
                    //有些数据在组件未启动时已经采集好. 所以有新的组件创建时要及时通知
                    synchronized (childComponentLock) {
                        childComponentLock.notifyAll();
                    }
                }

                //组件数据
            } else if (componentEvent instanceof ComponentChildInitializedEvent) {

                synchronized (childrenWaitRootReady) {
                    List<InitializedComponent> children = ((ComponentChildInitializedEvent) componentEvent).getChildren();

                    //没采集到数据则跳过
                    if (children.isEmpty()) {
                        return;
                    }

                    ComponentEnum parentComponent = children.get(0).getParent();

                    //1. 组件Root尚未创建, 则将其推送到等候队列.  当有新的组件被创建时, 会收到唤醒通知.
                    //2. 如果等待队列中采集到的数据刚好属于新组件, 那么采集到的数据会被设置到新组建, 然后将自身从等待队列中移除
                    InitializedComponent parentComponentObj = componentsRoot.get(parentComponent);
                    if (parentComponentObj == null) {
                        childrenWaitRootReady.put(parentComponent, children);
                        //将采集到的数据设置到组件中
                    } else {
                        parentComponentObj.insertChildren(children);
                    }
                }

            }

        }

    }

    @SneakyThrows
    @Override
    public void stop() {
        completion = true;
        synchronized (eventQueue) {
            eventQueue.notifyAll();
        }
        //避免线程无法释放
        synchronized (childComponentLock) {
            childComponentLock.notifyAll();
        }
        eventProcessed.await();

        componentsRoot.forEach((componentEnum, initializedComponent) -> {
            if (initializedComponent.isLazyRoot() && initializedComponent.getChildren().isEmpty()) {
                componentsRoot.remove(componentEnum);
            }
        });
        //
        springAgentStatistics.addInitializedComponents(componentsRoot.values());
    }

    @Override
    public int getOrder() {
        return LifeCycleOrdered.WAIT_COMPONENTS_STATISTICS;
    }

}
