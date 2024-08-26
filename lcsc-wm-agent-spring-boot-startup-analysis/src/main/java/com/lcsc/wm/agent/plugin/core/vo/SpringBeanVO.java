package com.lcsc.wm.agent.plugin.core.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.lcsc.wm.agent.core.vo.DurationVO;
import com.lcsc.wm.agent.plugin.core.enums.SpringBeanLifeCycleEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.*;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-12 01:10
 */
@Data
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class SpringBeanVO extends DurationVO {

    /**
     * 当前Bean的ID
     */
    private long id;

    /**
     * 当前bean的名字
     */
    private final String name;

    /**
     * 随着当前bean的初始化而加载的子bean
     */
    private List<SpringBeanVO> children;

    /**
     * parentId
     */
    private long parentId;

    /**
     * 实际加载耗时(减去创建依赖Bean的耗时)
     */
    private BigDecimal actualDuration;

    /**
     *
     */
    private Map<SpringBeanLifeCycleEnum, BeanLifeCycleDuration> beanLifeCycles;
    /**
     * 创建代理Bean耗时
     */
    private Map<String, Object> tags;

    public SpringBeanVO(long id, String name) {
        this.id = id;
        this.name = name;
        children = new ArrayList<>();
        tags = new HashMap<>();
    }

    @Override
    @JSONField(name = "duration")
    public BigDecimal getDuration() {
        return super.getDuration();
    }

    @Override
    @JSONField(name = "duration")
    public DurationVO setDuration(BigDecimal duration) {
        return super.setDuration(duration);
    }

    /**
     * 加载Bean的实际耗时
     *
     * @return
     */
    public BigDecimal getActualDuration() {
        Map<SpringBeanLifeCycleEnum, BeanLifeCycleDuration> beanLifeCycles = getBeanLifeCycles();
        if (beanLifeCycles == null) {
            return actualDuration;
        }

        BigDecimal actualDuration = this.actualDuration;
        for (Map.Entry<SpringBeanLifeCycleEnum, BeanLifeCycleDuration> entry : beanLifeCycles.entrySet()) {

            SpringBeanLifeCycleEnum lifeCycleEnum = entry.getKey();
            BeanLifeCycleDuration beanLifeCycleDuration = entry.getValue();
            switch (lifeCycleEnum) {
                case CreateAopProxyClass:
                case AfterPropertiesSet:
                case Others:
                    break;
                //统计耗时
                case AfterSingletonsInstantiated:
                    actualDuration = actualDuration.add(beanLifeCycleDuration.getDuration());
                    break;
                default:
                    log.error("Unknown lifeCycleEnum: {}, {}", name, lifeCycleEnum);
                    break;
            }

        }

        return actualDuration;
    }

    public void calcBeanLoadTime() {
        BigDecimal childrenDuration = children.stream().map(SpringBeanVO::getDuration)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        actualDuration = getDuration().subtract(childrenDuration);
    }

    public void addDependBean(SpringBeanVO dependBean) {
        dependBean.parentId = id;
        children.add(dependBean);
    }

    /**
     * 1.
     * -> 加载创建Bean自身的总耗时: actualDuration
     * ---> 生成代理Bean的耗时: createProxyDuration
     * 2.
     * -> 加载SmartInitializingBean耗时: smartInitializingDuration
     * 创建Bean的线程名(不出意外是main): threadName
     * 最终Bean的类名(如被aop代理, 则是代理类名): className
     * 创建Bean的类加载器: classLoader
     *
     * @param tagKey
     * @param tagValue
     */
    public void addTag(String tagKey, Object tagValue) {
        tags.put(tagKey, tagValue);
    }

    public void addBeanLifeCycle(SpringBeanLifeCycleEnum lifeCycleEnum, BeanLifeCycleDuration beanLifeCycleDuration) {
        if (beanLifeCycles == null) {
            beanLifeCycles = new LinkedHashMap<>();
        }
        beanLifeCycles.put(lifeCycleEnum, beanLifeCycleDuration);
    }

    @JSONField(name = "uuid")
    public String getUuid() {
        return UUID.randomUUID().toString();
    }

}
